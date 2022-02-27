# CLI Currency Converter

This program converts one currency into another at the current exchange rate.
This version on Russian language.

In the CLI interface, enter the amount and currency pair and from which currency to which you want to transfer in the format
[quantity] [code of the convertible currency] [code of the required currency]


Example

Command:
1000 usd rub

Output:
Total: 1000,00 US Dollar is equal to 75680.60 Russian Ruble

# How it works
The program takes the actual course in .xml format, then parses them using jsop, calculates and outputs the amount in another currency.


# How install
Telegram bot:
https://t.me/Cur_Convert_Bot

Command line interface:
java -Dfile.encoding=UTF-8 -jar Converter-Cli.jar
