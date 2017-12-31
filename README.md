# Crypto Portfolio
This application allows you to obtain the balances of your crypto wallets and calculate the equivalent amounts in fiat currencies.

[![asciicast](https://asciinema.org/a/fxSD6mvLHgorN6DC7ZIJeJBvN.png)](https://asciinema.org/a/fxSD6mvLHgorN6DC7ZIJeJBvN)

## Features
* Obtain balances of wallets
    * By public address (ETH, ETC, BTC, BTS)
    * Ethereum tokens
    * From Coinbase
* Obtain conversation rates from CoinMarketCap
* Calculate balances in fiat currencies

## Installation
The only system dependency is the [Java Runtime Environment (JRE)](https://www.java.com/en/download/).

Download the program and the sample configuration:

```shell
wget https://github.com/tindzk/crypto-portfolio/releases/download/v0.1/portfolio.jar
wget https://raw.githubusercontent.com/tindzk/crypto-portfolio/v0.1/wallets.sample.toml -O wallets.toml
```

Configure Crypto Portfolio by setting the public addresses of your wallets in `wallets.toml`.

## Usage
Run the program as follows:

```shell
java -jar portfolio.jar
```

## Donations
If you would like to support this project, donations are appreciated:

* BTS: crypto-portfolio

## Compilation
To compile Crypto Portfolio from the sources, you need to have [sbt](http://www.scala-sbt.org/download.html) installed.

The following command will create a self-contained JAR file that includes all dependencies:

```shell
sbt assembly
```

## Disclaimer
Crypto Portfolio uses public APIs to check the balance of your currencies and tokens. It does not have any access to your private keys.

## Licence
Crypto Portfolio is licensed under the terms of the Apache v2.0 licence.

## Contributors
* Tim Nieradzik
