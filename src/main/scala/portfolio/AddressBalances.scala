package portfolio

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scalaj.http.Http

object AddressBalances {
  case class EthplorerTokenInfo(symbol: String, decimals: Int)
  case class EthplorerToken(tokenInfo: EthplorerTokenInfo,
                            balance: BigDecimal)
  case class EthplorerEth(balance: Double)
  case class Ethplorer(ETH: EthplorerEth, tokens: List[EthplorerToken])
  case class GasTrackerBalance(ether: Double)
  case class GasTracker(balance: GasTrackerBalance)
  case class CryptoFreshBTS(balance: Double)
  case class CryptoFresh(BTS: CryptoFreshBTS)

  case class TokenBalance(name: String, balance: Double)
  case class Balance(balance: Double, tokens: List[TokenBalance])

  def fetchBalance(wallet: AddressWallet): Balance =
    wallet match {
      case AddressWallet(Ethereum, address, _) =>
        val contents = Http(
          s"https://api.ethplorer.io/getAddressInfo/$address?apiKey=freekey"
        ).asString

        val response = decode[Ethplorer](contents.body).right.get
        Balance(response.ETH.balance,
          response.tokens.map(t =>
            TokenBalance(
              t.tokenInfo.symbol,
              (t.balance / math.pow(10, t.tokenInfo.decimals)).toDouble)))

      case AddressWallet(EthereumClassic, address, _) =>
        val contents = Http(s"https://api.gastracker.io/addr/$address").asString
        val balance = decode[GasTracker](contents.body).right.get.balance.ether
        Balance(balance, List.empty)

      case AddressWallet(Bitcoin, address, _) =>
        val contents = Http(s"https://blockchain.info/q/addressbalance/$address?confirmations=6").asString
        val balance = contents.body.toDouble / 100000000L
        Balance(balance, List.empty)

      case AddressWallet(Bitshares, address, _) =>
        val contents = Http(s"https://cryptofresh.com/api/account/balances?account=$address").asString
        val balance = decode[CryptoFresh](contents.body).right.get.BTS.balance
        Balance(balance, List.empty)
    }
}
