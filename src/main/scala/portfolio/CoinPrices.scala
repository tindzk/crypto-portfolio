package portfolio

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scalaj.http.Http

object CoinPrices {
  case class CoinMarketCapCoin(symbol   : String,
                               price_usd: Double,
                               price_eur: Double)

  // currency symbol -> price
  type Prices = Map[String, Double]

  // coin symbol -> prices
  type CoinPrices = Map[String, Prices]

  def fetch(coins: Set[String], currencies: Set[String]): CoinPrices = {
    // TODO Consider all `currencies`
    // Assume that the coins are among the top 400
    val contents = Http(s"https://api.coinmarketcap.com/v1/ticker/?convert=EUR&limit=400").asString
    decode[List[CoinMarketCapCoin]](contents.body).right.get.map { coin =>
      coin.symbol -> Map("EUR" -> coin.price_eur, "USD" -> coin.price_usd)
    }.toMap
  }
}
