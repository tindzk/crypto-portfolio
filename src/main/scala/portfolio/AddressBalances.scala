package portfolio

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scalaj.http.Http

object AddressBalances {
  case class EtherchainDatum(address: String, balance: Long)
  case class Etherchain(data: List[EtherchainDatum])
  case class GasTrackerBalance(ether: Double)
  case class GasTracker(balance: GasTrackerBalance)
  case class CryptoFreshBTS(balance: Double)
  case class CryptoFresh(BTS: CryptoFreshBTS)

  def fetchBalance(wallet: AddressWallet): Double =
    wallet match {
      case AddressWallet(Ethereum, address) =>
        val contents = Http(s"https://etherchain.org/api/account/$address").asString
        decode[Etherchain](contents.body).right.get.data.head.balance.toDouble / 1000000000000000000L

      case AddressWallet(EthereumClassic, address) =>
        val contents = Http(s"https://api.gastracker.io/addr/$address").asString
        decode[GasTracker](contents.body).right.get.balance.ether

      case AddressWallet(Bitcoin, address) =>
        val contents = Http(s"https://blockchain.info/q/addressbalance/$address?confirmations=6").asString
        contents.body.toDouble / 100000000L

      case AddressWallet(Bitshares, address) =>
        val contents = Http(s"https://cryptofresh.com/api/account/balances?account=$address").asString
        decode[CryptoFresh](contents.body).right.get.BTS.balance
    }
}
