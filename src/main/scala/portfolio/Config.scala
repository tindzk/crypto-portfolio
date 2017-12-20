package portfolio

import java.io.File

import toml._
import toml.Codecs._

object Config {
  implicit val currencyCodec: Codec[Currency] = Codec {
    case (Value.Str(value), _) =>
      value match {
        case "ethereum"         | "ETH" => Right(Ethereum)
        case "ethereum-classic" | "ETC" => Right(EthereumClassic)
        case "bitcoin"          | "BTC" => Right(Bitcoin)
        case "bitcoin-cash"     | "BCH" => Right(BitcoinCash)
        case "bitshares"        | "BTS" => Right(Bitshares)
        case "litecoin"         | "LTC" => Right(Litecoin)
        case _ => Left((List.empty, s"Invalid currency: $value"))
      }

    case (value, _) => Left((List.empty, s"Currency expected, $value provided"))
  }

  case class Coinbase(apiKey: String, apiSecret: String, wallets: List[String])
  case class Configuration(currencies: List[String],
                           wallet    : List[AddressWallet],
                           coinbase  : Option[Coinbase])

  def load(file: File): Either[Codec.Error,Configuration] = {
    val toml = scala.io.Source.fromFile(file).mkString
    Toml.parseAs[Configuration](toml)
  }
}
