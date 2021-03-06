package portfolio

import java.io.File

object Main extends App {
  def walletCurrencies(wallets: List[Wallet],
                       cbBalances: Option[CoinbaseApi.Balances]
                      ): List[String] =
    wallets.flatMap {
      case a: AddressWallet  => List(a.currency.symbol) ++ a.tokens
      case c: CoinbaseWallet =>
        c.walletNames.map(cbBalances.get).map(_._1.symbol)
    }

  def walletNames(wallets: List[Wallet],
                  walletCurrencies: List[String]
                 ): List[String] =
    wallets.flatMap {
      case a: AddressWallet  => List("Address wallet") ++
        a.tokens.map(_ => "Token")
      case c: CoinbaseWallet => c.walletNames.map(_ => "Coinbase wallet")
    }.zip(walletCurrencies).map { case (walletName, currency) =>
      s"$walletName ($currency)"
    }

  def walletBalances(wallets: List[Wallet],
                     cbBalances: Option[CoinbaseApi.Balances]
                    ): List[Double] =
    wallets.flatMap {
      case a: AddressWallet =>
        println(s"Fetching balance for ${a.currency} wallet...")
        val wallet = AddressBalances.fetchBalance(a)
        List(wallet.balance) ++ a.tokens.map(t =>
          wallet.tokens
            .collectFirst { case tk if tk.name == t => tk.balance }
            .getOrElse(0.0)
        )

      case c: CoinbaseWallet =>
        cbBalances match {
          case None     => List.empty
          case Some(cb) => c.walletNames.map(cb(_)._2)
        }
    }

  def load(fiatCurrencies: List[String],
           wallets       : List[Wallet],
           coinbaseAuth  : Option[(String, String)]): Unit = {
    val tokens =
      wallets.collect { case a: AddressWallet => a.tokens.length }.sum

    println(s"Found ${wallets.length} wallets, $tokens tokens")

    val cbBalances = coinbaseAuth.map { case (apiKey, apiSecret) =>
      println("Fetching Coinbase balances...")
      CoinbaseApi.fetchBalances(apiKey, apiSecret)
    }

    val currencies = walletCurrencies(wallets, cbBalances)

    println("Fetching conversion rates...")
    val coinPrices = CoinPrices.fetch(currencies.toSet, fiatCurrencies.toSet)

    val balances = walletBalances(wallets, cbBalances)

    val converted = currencies.zip(balances).map { case (currency, balance) =>
      fiatCurrencies.map { fiatCurrency =>
        fiatCurrency -> balance * coinPrices(currency)(fiatCurrency)
      }.toMap
    }

    println()
    println("## Portfolio")
    println()

    val names = walletNames(wallets, currencies)
    names.zip(currencies).zip(balances).zip(converted).foreach {
      case (((name, currency), balance), converted) =>
        println(name)
        println(s"  Balance: $balance")
        converted.foreach { case (symbol, amount) =>
          println(s"      $symbol: $amount")
        }
    }

    println("Total:")
    fiatCurrencies.foreach { symbol =>
      val amount = converted.map(_(symbol)).sum
      println(s"  $symbol: $amount")
    }
  }

  Config.load(new File("wallets.toml")) match {
    case Left(l) =>
      println(l)
      sys.exit(1)

    case Right(cfg) =>
      val cbWallets = cfg.coinbase
        .map(cb => CoinbaseWallet(cb.wallets))
        .toList

      load(cfg.currencies, cfg.wallet ++ cbWallets, cfg.coinbase.map { cb =>
        (cb.apiKey, cb.apiSecret)
      })
  }
}
