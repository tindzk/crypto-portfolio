# Crypto Portfolio
This application allows you to obtain the balances of your crypto wallets and
calculate the equivalent amounts in fiat currencies.

[![asciicast](https://asciinema.org/a/fxSD6mvLHgorN6DC7ZIJeJBvN.png)](https://asciinema.org/a/fxSD6mvLHgorN6DC7ZIJeJBvN)

## Features
* Obtain balances of wallets
    * By public address (ETH, ETC, BTC, BTS)
    * From Coinbase
* Obtain conversation rates from CoinMarketCap

## Compilation
The following command creates a self-contained JAR file that includes all dependencies:

```shell
sbt assembly
```

Copy the sample configuration from `wallets.sample.toml` to `wallets.toml` and
set the public addresses of your wallets.

Run the program as follows:

```bash
java -jar portfolio.jar
```

## Donations
If you would like to support this project, donations are appreciated:

* BTS: crypto-portfolio

## Licence
Crypto Portfolio is licensed under the terms of the Apache v2.0 licence.

## Contributors
* Tim Nieradzik
