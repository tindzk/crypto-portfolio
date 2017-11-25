package portfolio

import java.io.File

import toml._
import toml.Codecs._

object Config {
  implicit val currencyCodec: Codec[Currency] = Codec {
    case (Value.Str(value), _) =>
      val currency = value match {
        case "ethereum"         | "ETH" => Some(Ethereum)
        case "ethereum-classic" | "ETC" => Some(EthereumClassic)
        case "bitcoin"          | "BTC" => Some(Bitcoin)
        case "bitshares"        | "BTS" => Some(Bitshares)
        case "litecoin"         | "LTC" => Some(Litecoin)
        case _ => None
      }

      currency match {
        case None => Left((List.empty, s"Invalid currency: $value"))
        case Some(c) => Right(c)
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
