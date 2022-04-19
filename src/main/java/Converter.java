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
    private static final Map<String, Coin> allCoins = new HashMap<>();

    public static void createCoinMap(Document document) {
        for (Element element : document.select("Valute")) {
            allCoins.put(element.select("CharCode").text(),
                    new Coin(
                            element.select("CharCode").text(),
                            element.select("Name").text(),
                            Double.parseDouble(element.select("Value").text().replace(",", "."))
                                    / Integer.parseInt(element.select("Nominal").text())
                    )
            );
        }
        allCoins.put("RUB", new Coin("RUB", "Российский рубль", 1.0));
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
                / desireCoin.getValueCoin();
    }

    public static String viewCoinTable() {
        ArrayList<String> keyList = new ArrayList<>();
        Stream<Map.Entry<String, Coin>> stream = allCoins.entrySet().stream();
        stream.sorted(Comparator.comparing(e -> e.getValue().getName())).forEach(e -> keyList.add(e.getValue().getCharCode()));

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : keyList) {
            stringBuilder.append(String.format("%s - %s", allCoins.get(s).getCharCode(), allCoins.get(s).getName())).append("\n");
        }
        return stringBuilder.toString();
    }

    public static Map<String, Coin> getAllCoins() {
        return allCoins;
    }

    public static void updateCurrencyValue() {
        String responseData = Converter.makeRequest();
        Document document = Jsoup.parse(responseData, "", Parser.xmlParser());
        Converter.createCoinMap(document);
    }

}

