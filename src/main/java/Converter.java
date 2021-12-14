import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Converter {
    private static final List<Currency> allCurrency = new ArrayList<>();
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static String data;
    private static int quantity;
    private static Currency firstCurrencyObject = null;
    private static Currency secondCurrencyObject = null;

    public static void main(String[] args) throws IOException {
        System.out.println("Добро пожаловать в конвертер валют. " +
                "\nДля выхода наберите \"exit\", для обзора и курса валют \"view\", " +
                "\nдля просмотра всех кодов наберите \"viewcode\"");
        System.out.println();
        makeRequest();
        createCurrency();
        cli();
        reader.close();
    }

    // Получаем данные
    public static void makeRequest() {
        URL url = null;
        try {
            url = new URL("https://www.cbr-xml-daily.ru/daily_utf8.xml");
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        InputStream input = null;
        try {
            if (url != null) {
                input = url.openStream();
            }
            byte[] buffer = new byte[0];           //Здесь хранятся все данные
            if (input != null) {
                buffer = input.readAllBytes();
            }
//            System.out.println(new String(buffer));       //Этим можно посмотреть их
            data = new String(buffer);
            if (input != null) {
                input.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public static void createCurrency() {
        Document document = Jsoup.parse(data, "", Parser.xmlParser());           // 4 раза пройтись тэгами, 34 раза создать currency. Что бы создать объект нужно, чтобы i(34)-элементы создались по 4(j)
        String[] tags = {"CharCode", "Name", "Value", "Nominal"};
        Elements elements = document.select(tags[0]);                                   // Для определения количества currency

        for (int i = 0; i < elements.size(); i++) {
            String[] currencyElements = new String[4];                                  // Здесь будут временно храниться данные 4 элементов для каждого Currency
            for (int j = 0; j < 4; j++) {
                elements = document.select(tags[j]);                                    // Тут выделяется пачка элементов. Из нее надо по одному элементу записывать в currencyElement
                currencyElements[j] = elements.get(i).text();
            }
            allCurrency.add(new Currency(currencyElements[0],
                    currencyElements[1],
                    Double.parseDouble(currencyElements[2].replace(",", ".")),
                    Integer.parseInt(currencyElements[3])));
        }
        allCurrency.add(new Currency("RUB", "Российский рубль", 1.0, 1));
    }


    public static void viewCurrencyTable() {
        for (Currency currency : allCurrency) {
            System.out.println(String.format("Валюта: %s | Курс к рублю: %.2f | Код валюты: %s | Номинал: %d",
                    currency.getName(), currency.getValue(), currency.getCharCode(), currency.getNominal()));
        }
        System.out.println();
    }

    public static void viewCurrencyCode() {
        for (Currency currency : allCurrency) {
            System.out.println(String.format(" %s - %s",
                    currency.getCharCode(), currency.getName()));
        }
        System.out.println();
    }


    public static double converter(int quantity, String firstCurrency, String desireCurrency) {
        int currencyIndex = 0;
        int desireIndex = 0;

        for (int i = 0; i < allCurrency.size(); i++) {
            if ((allCurrency.get(i).getCharCode()).equalsIgnoreCase(firstCurrency)) {
                currencyIndex = i;
            }
            if ((allCurrency.get(i).getCharCode()).equalsIgnoreCase(desireCurrency)) {
                desireIndex = i;
            }
        }

        firstCurrencyObject = allCurrency.get(currencyIndex);
        secondCurrencyObject = allCurrency.get(desireIndex);

        return quantity * firstCurrencyObject.getValue()
                / secondCurrencyObject.getValue()
                * secondCurrencyObject.getNominal();
    }


    public static void cli() {
        String[] line;
        while (true) {
            System.out.println("Введите валютную пару в формате [код валюты] [код валюты]:");
            try {
                line = reader.readLine().split(" ");
                if (line.length == 2) {
                    String currencyCode = line[0];
                    String desireCurrency = line[1];
                    if (checkAvailability(currencyCode) && checkAvailability(desireCurrency)) {
                        quantityIn(currencyCode, desireCurrency);
                        double result = converter(quantity, currencyCode, desireCurrency);
                        System.out.printf("Итого: %d %s равен %s %s \n", quantity, firstCurrencyObject.getName(), String.format("%.2f", result), secondCurrencyObject.getName());
                    } else System.out.println("Такой валюты не существует.");
                } else if (line[0].equalsIgnoreCase("view")) {
                    viewCurrencyTable();
                } else if (line[0].equalsIgnoreCase("viewcode")) {
                    viewCurrencyCode();
                } else if (line[0].equalsIgnoreCase("exit")) {
                    break;
                } else System.out.println("Валюта введена некорректно. Для просмотра кодов наберите \"viewcode\"");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода в cli");
                e.printStackTrace();
            }
        }
    }

    public static void quantityIn(String currencyCode, String desireCurrency) {
        try {
            System.out.println("Сколько " + currencyCode.toUpperCase() + " Вы хотите перевести в " + desireCurrency.toUpperCase() + "?");
            quantity = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Введен неверный формат. Повторите, пожалуйста, ввод:");
            quantityIn(currencyCode, desireCurrency);
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода в quantityIn");
            e.printStackTrace();
        }
    }

    public static boolean checkAvailability(String curCode) {
        for (int i = 0; i < allCurrency.size(); i++) {
            if ((allCurrency.get(i).getCharCode()).equalsIgnoreCase(curCode)) {
                return true;
            }
        }
        return false;
    }
}
