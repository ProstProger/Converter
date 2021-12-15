import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Converter {

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в конвертер валют. " +
                "\nДля выхода наберите \"exit\", для обзора и курса валют \"view\", " +
                "\nдля просмотра всех кодов наберите \"viewcode\"\n");
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Currency> allCurrency = createCurrency();
        String[] line = null;
        boolean exit = false;

        while (!exit) {
            System.out.println("Введите валютную пару в формате [код валюты] [код валюты]:");
            try {
                line = reader.readLine().split(" ");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода в cli");
                e.printStackTrace();
            }

            if ((line != null ? line.length : 0) == 2) {
                String currencyCode = line[0];
                String desireCurrency = line[1];
                if (checkAvailability(allCurrency, currencyCode) && checkAvailability(allCurrency, desireCurrency)) {
                    double quantity = quantityIn(currencyCode, desireCurrency);
                    double result = converter(allCurrency, quantity, currencyCode, desireCurrency);
                    System.out.printf("Итого: %.2f %s равен %s %s \n", quantity, allCurrency.get(searchIndex(allCurrency, currencyCode)).getName(), String.format("%.2f", result), allCurrency.get(searchIndex(allCurrency, desireCurrency)).getName());
                } else System.out.println("Такой валюты не существует. Для просмотра кодов наберите \"viewcode\"");
            } else switch (line[0]) {
                case ("view"):
                    viewCurrencyTable(allCurrency);
                    break;
                case ("viewcode"):
                    viewCurrencyCode(allCurrency);
                    break;
                case ("exit"):
                    exit = true;
                    break;
                default:
                    System.out.println("Такой валюты не существует. Для просмотра кодов наберите \"viewcode\"");
            }
        }
        // Здесь можно сделать try-with-resource, чтобы не писать reader.close(). Но получится, что он на весь блок.
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Currency> createCurrency() {
        ArrayList<Currency> list = new ArrayList<>();
        // Необходимо 4 раза пройтись тэгами, 34 раза создать currency. Что бы создать объект нужно, чтобы i(34)-элементы создались по 4(j)
        Document document = Jsoup.parse(makeRequest(), "", Parser.xmlParser());
        String[] tags = {"CharCode", "Name", "Value", "Nominal"};
        // Для определения количества currency
        Elements elements = document.select(tags[0]);

        for (int i = 0; i < elements.size(); i++) {
            // Здесь будут временно храниться данные 4 элементов для каждого Currency
            String[] currencyElements = new String[4];
            for (int j = 0; j < 4; j++) {
                // Тут выделяется пачка элементов. Из нее надо по одному элементу записывать в currencyElement
                elements = document.select(tags[j]);
                currencyElements[j] = elements.get(i).text();
            }
            list.add(new Currency(currencyElements[0],
                    currencyElements[1],
                    Double.parseDouble(currencyElements[2].replace(",", ".")),
                    Integer.parseInt(currencyElements[3])));
        }
        list.add(new Currency("RUB", "Российский рубль", 1.0, 1));
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

    public static double converter(ArrayList<Currency> list, double quantity, String firstCurrency, String desireCurrency) {
        int currencyIndex = searchIndex(list, firstCurrency);
        int desireIndex = searchIndex(list, desireCurrency);
        return quantity * list.get(currencyIndex).getValue()
                / list.get(desireIndex).getValue()
                * list.get(desireIndex).getNominal();
    }

    public static int searchIndex(ArrayList<Currency> list, String currencyCode) {
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).getCharCode()).equalsIgnoreCase(currencyCode)) {
                return i;
            }
        }
        return 0;
    }

    public static double quantityIn(String currencyCode, String desireCurrency) {
        // Тут создается еще один reader, к сожалению...
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(System.in));
        // Закрыть ридер не получается. При закрытии в любом случе - loop и экраны ошибок. Так в этом месте и не разобрался.
        // Все таки ридер лучше что был один и глобальный. По крайней мере это выход.

        double x = 0;
        try {
            String line = "";
            while (true) {
                System.out.println("Сколько " + currencyCode.toUpperCase() + " Вы хотите перевести в " + desireCurrency.toUpperCase() + "?");
                line = reader2.readLine();
                try {
                    x = Double.parseDouble(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Введено не число. Повторите, пожалуйста, ввод:");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода в quantityIn");
            e.printStackTrace();
        }

        return x;
    }

    public static boolean checkAvailability(ArrayList<Currency> list, String curCode) {
        for (Currency currency : list) {
            if ((currency.getCharCode()).equalsIgnoreCase(curCode)) {
                return true;
            }
        }
        return false;
    }

    public static void viewCurrencyTable(ArrayList<Currency> list) {
        for (Currency currency : list) {
            System.out.println(String.format("Валюта: %s | Курс к рублю: %.2f | Код валюты: %s | Номинал: %d",
                    currency.getName(), currency.getValue(), currency.getCharCode(), currency.getNominal()));
        }
        System.out.println();
    }

    public static void viewCurrencyCode(ArrayList<Currency> list) {
        for (Currency currency : list) {
            System.out.println(String.format("%s - %s",
                    currency.getCharCode(), currency.getName()));
        }
        System.out.println();
    }
}

/*Converter.java
1. Убираем все глобальные переменные https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L15-L20
2. Методы makeRequest() и createCurrency() надо перенести внутрь cli(), как и все принты в main-e https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L27-L28
3. Метод makeRequest должен отдавать string и принимать аргумент string, то есть выглядеть как-то так public static string makeRequest(string url)
4. Метод createCurrency() должен отдавать list и пинимать аргумент string, то есть выглядеть как-то так public static list createCurrency(string response)
5. В методе cli() try-catch нужен только для строки line = reader.readLine().split(" "), поэтому все if можно из под него убрать https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L125
6. Перепиши else if на switch case https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L134-L138
7. Тут лучше писать не if - else if - else if и т.д., а что-то типа if - else и внутри else пишешь switch case из пункта 6 https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L126-L140
8. Располагай комментарии к строка над ними вот так: https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L33-L34 а не вот так https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L62
9. Лишний try-catch по-моему тут не будет никакого эксепшена тут просто создается инстанс https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L36-L40 и тогда вот эта проверка тоже лишняя так как мы всегда создадим объект типа URL https://github.com/ProstProger/Converter2/blob/main/src/main/java/Converter.java#L43
--*/