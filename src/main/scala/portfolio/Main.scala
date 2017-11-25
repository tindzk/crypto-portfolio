package portfolio

import java.io.File

object Main extends App {
  def load(fiatCurrencies: List[String],
           wallets       : List[Wallet],
           coinbaseAuth  : Option[(String, String)]): Unit = {
    println(s"Found ${wallets.length} wallets")

    val cbBalances = coinbaseAuth.map { case (apiKey, apiSecret) =>
      println("Fetching Coinbase balances...")
      CoinbaseApi.fetchBalances(apiKey, apiSecret)
    }

    val currencies = wallets.map {
      case a: AddressWallet  => a.currency.symbol
      case c: CoinbaseWallet => cbBalances.get(c.name)._1.symbol
    }

    println("Fetching conversion rates...")
    val coinPrices = CoinPrices.fetch(currencies.toSet, fiatCurrencies.toSet)

    val balances = wallets.map {
      case a: AddressWallet =>
        println(s"Fetching balance for ${a.currency} wallet...")
        AddressBalances.fetchBalance(a)

      case c: CoinbaseWallet =>
        cbBalances match {
          case None     => 0.0
          case Some(cb) => cb(c.name)._2
        }
    }

    val converted = currencies.zip(balances).map { case (currency, balance) =>
      fiatCurrencies.map { fiatCurrency =>
        fiatCurrency -> balance * coinPrices(currency)(fiatCurrency)
      }.toMap
    }

    println()
    println("## Portfolio")
    println()

    wallets.zip(currencies).zip(balances).zip(converted).foreach {
      case (((wallet, currency), balance), converted) =>
        wallet match {
          case _: AddressWallet  => println(s"Address wallet ($currency)")
          case _: CoinbaseWallet => println(s"Coinbase wallet ($currency)")
        }

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
        .map(cb => cb.wallets.map(CoinbaseWallet))
        .getOrElse(List.empty)

      load(cfg.currencies, cfg.wallet ++ cbWallets, cfg.coinbase.map { cb =>
        (cb.apiKey, cb.apiSecret)
      })
  }
}
