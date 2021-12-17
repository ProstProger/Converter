import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class Converter {

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в конвертер валют. " +
                "\nДля выхода наберите \"exit\", для обзора и курса валют \"view\", " +
                "\nдля просмотра кодов наберите \"viewcode\"\n");
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Coin> allCoin = createCoin();
        String[] line = null;
        boolean exit = false;

        while (!exit) {
            System.out.println("Введите количество и валютную пару в формате\n[количество] [код валюты] [код валюты]:");
            try {
                line = reader.readLine().split(" ");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода в cli");
                e.printStackTrace();
            }
            if (line != null) {
                if (line.length == 3) {
                    double quantity = 0.0;
                    try {
                        quantity = Double.parseDouble(line[0]);

                    } catch (NumberFormatException ignore) {
                    }
                    String convertCoin = line[1].toUpperCase();
                    String desireCoin = line[2].toUpperCase();
                    if (allCoin.containsKey(convertCoin) && allCoin.containsKey(desireCoin)) {
                        double result = converter(quantity, allCoin.get(convertCoin), allCoin.get(desireCoin));
                        System.out.printf("Итого: %.2f %s равен %s %s \n", quantity, allCoin.get(convertCoin).getName(), String.format("%.2f", result), allCoin.get(desireCoin).getName());
                    } else
                        System.out.println("Введен неправильный формат, либо такой валюты не существует.\nДля просмотра кодов наберите \"viewcode\"");
                } else switch (line[0]) {
                    case ("view"):
                        viewCoinTable(allCoin, 'a');
                        break;
                    case ("viewcode"):
                        viewCoinTable(allCoin, 'b');
                        break;
                    case ("exit"):
                        exit = true;
                        break;
                    default:
                        System.out.println("Такой команды нет");
                }
            }
        }
        // Здесь можно сделать try-with-resource, чтобы не писать reader.close(). Но получится, что он на весь блок.
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Coin> createCoin() {
        Map<String, Coin> list = new HashMap<>();
        // Необходимо 4 раза пройтись тэгами, 34 раза создать coin. Что бы создать объект нужно, чтобы i(34)-элементы создались по 4(j)
        Document document = Jsoup.parse(makeRequest(), "", Parser.xmlParser());
        String[] tags = {"CharCode", "Name", "Value", "Nominal"};
        // Для определения количества coin
        Elements elements = document.select(tags[0]);

        for (int i = 0; i < elements.size(); i++) {
            // Здесь будут временно храниться данные 4 элементов для каждого Coin
            String[] coinElements = new String[4];
            for (int j = 0; j < 4; j++) {
                // Тут выделяется пачка элементов. Из нее надо по одному элементу записывать в coinElement
                elements = document.select(tags[j]);
                coinElements[j] = elements.get(i).text();
            }
            list.put(coinElements[0].toUpperCase(), new Coin(coinElements[0].toUpperCase(),
                    coinElements[1],
                    Double.parseDouble(coinElements[2].replace(",", ".")),
                    Integer.parseInt(coinElements[3])));
        }
        list.put("RUB", new Coin("RUB", "Российский рубль", 1.0, 1));
        return list;
    }

    // Получаем данные
    // throws MalformedURLException придется добавить если без try URL обрабатывать. Насколько я знаю так нехорошо делать.
    public static String makeRequest() {
        byte[] buffer = new byte[0];
        URL url = null;
        try {
            url = new URL("https://www.cbr-xml-daily.ru/daily_utf8.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream input = null;
        try {
            assert url != null;
            input = url.openStream();
            //Здесь хранятся все данные
            buffer = input.readAllBytes();
//            System.out.println(new String(buffer));       //Этим можно посмотреть строчку
            input.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return new String(buffer);
    }

    // В теории и от этого метода можно избавиться
    public static double converter(double quantity, Coin convertCoin, Coin desireCoin) {
        return quantity * convertCoin.getValueCoin()
                / desireCoin.getValueCoin()
                * desireCoin.getNominal();
    }


    public static void viewCoinTable(Map<String, Coin> coinMap, char parameter) {
        // Для вывода списка по алфавиту
        ArrayList<String> keyList = new ArrayList<>();
        Stream<Map.Entry<String, Coin>> stream = coinMap.entrySet().stream();
        stream.sorted(Comparator.comparing(e -> e.getValue().getName())).forEach(e -> keyList.add(e.getValue().getCharCode()));

        for (String s : keyList) {
            // Может все таки оставить обзор валют?)
            if (parameter == 'a') {
                System.out.println(String.format("Валюта: %s | Курс к рублю: %.2f | Код валюты: %s | Номинал: %d",
                        coinMap.get(s).getName(), coinMap.get(s).getValueCoin(), coinMap.get(s).getCharCode(), coinMap.get(s).getNominal()));
            } else {
                System.out.println(String.format("%s - %s", coinMap.get(s).getCharCode(), coinMap.get(s).getName()));
            }
        }
        System.out.println();
    }
}
