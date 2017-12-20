package portfolio

import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Hex

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scalaj.http.Http

object CoinbaseApi {
  val ApiVersion = "2017-11-24"

  case class Balance(amount: Double, currency: String)
  case class Datum(name: String, balance: Balance)
  case class Response(data: List[Datum])

  def hmacHexEncode(hmacType: String, apiSecret: String, message: String): String = {
    val keySpec = new SecretKeySpec(apiSecret.getBytes(), hmacType)

    val mac = Mac.getInstance(hmacType)
    mac.init(keySpec)

    val rawHmac = mac.doFinal(message.getBytes)
    Hex.encodeHexString(rawHmac)
  }

  /** @return wallet name -> (currency, amount) */
  def fetchBalances(apiKey: String, apiSecret: String): Map[String, (Currency, Double)] = {
    val timestamp   = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    val method      = "GET"
    val requestPath = "/v2/accounts"
    val url         = s"https://api.coinbase.com$requestPath"
    val body        = ""
    val hmacType    = "HmacSHA256"
    val message     = timestamp + method + requestPath + body

    val response = Http(url)
      .header("CB-ACCESS-SIGN"     , hmacHexEncode(hmacType, apiSecret, message))
      .header("CB-ACCESS-TIMESTAMP", timestamp.toString)
      .header("CB-ACCESS-KEY"      , apiKey)
      .header("CB-VERSION"         , ApiVersion)

    decode[Response](response.asString.body).right.get.data
      .filter(d => !Set("EUR", "USD").contains(d.balance.currency))
      .map { d =>
        val c = d.balance.currency match {
          case "BTC" => Bitcoin
          case "BCH" => BitcoinCash
          case "LTC" => Litecoin
          case "ETH" => Ethereum
        }

        d.name -> (c, d.balance.amount)
      }.toMap
  }
}
