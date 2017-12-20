package portfolio

sealed abstract class Currency(val symbol: String)
case object Ethereum        extends Currency("ETH")
case object EthereumClassic extends Currency("ETC")
case object Bitcoin         extends Currency("BTC")
case object BitcoinCash     extends Currency("BCH")
case object Litecoin        extends Currency("LTC")
case object Bitshares       extends Currency("BTS")

sealed trait Wallet
case class AddressWallet(currency: Currency, address: String) extends Wallet
case class CoinbaseWallet(name: String) extends Wallet
