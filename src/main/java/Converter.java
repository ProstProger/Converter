import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class Converter {

    public static void main(String[] args) {
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String responseData = makeRequest();
        Document document = Jsoup.parse(responseData, "", Parser.xmlParser());
        Map<String, Coin> allCoin = createCoinMap(document);
        String[] line = null;
        boolean exit = false;

        System.out.println("Добро пожаловать в конвертер валют. " +
                "\nДля выхода наберите \"exit,\" для просмотра кодов наберите \"view\"\n");
            System.out.println("Введите количество и из какой валюты в какую хотите перевести в формате\n[количество] [код конвертируемой валюты] [код требуемой валюты]:");
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
                            double result = makeConvertation(quantity, allCoin.get(convertCoin), allCoin.get(desireCoin));
                            System.out.printf("Итого: %.2f %s равен %s %s \n", quantity, allCoin.get(convertCoin).getName(), String.format("%.2f", result), allCoin.get(desireCoin).getName());
                        } else
                            System.out.println("Введен неправильный формат, либо такой валюты не существует.\nДля просмотра кодов наберите \"view\"");
                    } else switch (line[0]) {
                        case ("view"):
                            viewCoinTable(allCoin);
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

    public static Map<String, Coin> createCoinMap(Document document) {
        Map<String, Coin> coinsMap = new HashMap<>();

        for (Element element : document.select("Valute")) {
            coinsMap.put(element.select("CharCode").text(),
                    new Coin(
                            element.select("CharCode").text(),
                            element.select("Name").text(),
                            Double.parseDouble(element.select("Value").text().replace(",", ".")),
                            Integer.parseInt(element.select("Nominal").text())
                    )
            );
        }
        coinsMap.put("RUB", new Coin("RUB", "Российский рубль", 1.0, 1));
        return coinsMap;
    }

    public static String makeRequest() {
        byte[] buffer = new byte[0];
        URL url = null;
        try {
            url = new URL("https://www.cbr-xml-daily.ru/daily_utf8.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert url != null;
            InputStream input = url.openStream();
            buffer = input.readAllBytes();
            input.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return new String(buffer);
    }

    public static double makeConvertation(double quantity, Coin convertCoin, Coin desireCoin) {
        return quantity * convertCoin.getValueCoin()
                / desireCoin.getValueCoin()
                * desireCoin.getNominal();
    }

    public static void viewCoinTable(Map<String, Coin> coinMap) {
        ArrayList<String> keyList = new ArrayList<>();
        Stream<Map.Entry<String, Coin>> stream = coinMap.entrySet().stream();
        stream.sorted(Comparator.comparing(e -> e.getValue().getName())).forEach(e -> keyList.add(e.getValue().getCharCode()));

        for (String s : keyList) {
            System.out.println(String.format("%s - %s", coinMap.get(s).getCharCode(), coinMap.get(s).getName()));

        }
        System.out.println();
    }
}
