import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Cli {

    public static void main(String[] args) {
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String responseData = Converter.makeRequest();
        Document document = Jsoup.parse(responseData, "", Parser.xmlParser());
        Map<String, Coin> allCoin = Converter.createCoinMap(document);
        String[] line = null;
        boolean exit = false;

        System.out.println("Добро пожаловать в конвертер валют. " +
                "\nДля выхода наберите \"exit\", для просмотра кодов наберите \"view\"\n");
        System.out.println("Введите количество и из какой валюты в какую хотите перевести в формате\n" +
                "[количество] [код конвертируемой валюты] [код требуемой валюты]:\n" +
                "Например: 1000 usd rub");
        while (!exit) {
            try {
                line = reader.readLine().split(" ");

            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода в cli");
                e.printStackTrace();
            }
            if (line != null) {
                if (line.length == 3) {
                    double quantity;
                    try {
                        quantity = Double.parseDouble(line[0]);
                    } catch (NumberFormatException e) {
                        System.out.println("Первым параметром введено не число, повторите ввод.");
                        continue;
                    }
                    String convertCoin = line[1].toUpperCase();
                    String desireCoin = line[2].toUpperCase();
                    if (allCoin.containsKey(convertCoin) && allCoin.containsKey(desireCoin)) {
                        double result = Converter.makeConvertation(quantity, allCoin.get(convertCoin), allCoin.get(desireCoin));
                        System.out.printf("Итого: %.2f %s равен %s %s \n", quantity, allCoin.get(convertCoin).getName(), String.format("%.2f", result), allCoin.get(desireCoin).getName());
                    } else
                        System.out.println("Введен неправильный формат, либо такой валюты не существует.\nДля просмотра кодов наберите \"view\"");
                } else switch (line[0]) {
                    case ("view"):
                        Converter.viewCoinTable(allCoin);
                        break;
                    case ("exit"):
                        exit = true;
                        break;
                    default:
                        System.out.println("Такой команды нет.");
                }
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
