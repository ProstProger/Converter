import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cli {
    private static UpdateCurrencyValue updateValue = new UpdateCurrencyValue();

    public static void main(String[] args) {
        updateValue.start();
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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

                    if (Converter.getAllCoins().containsKey(convertCoin) && Converter.getAllCoins().containsKey(desireCoin)) {
                        double result = Converter.makeConvertation(quantity, Converter.getAllCoins().get(convertCoin), Converter.getAllCoins().get(desireCoin));
                        System.out.printf("Итого: %.2f %s равен %s %s \n", quantity, Converter.getAllCoins().get(convertCoin).getName(), String.format("%.2f", result), Converter.getAllCoins().get(desireCoin).getName());
                    } else
                        System.out.println("Введен неправильный формат, либо такой валюты не существует.\nДля просмотра кодов наберите \"view\"");
                } else switch (line[0]) {
                    case ("view"):
                        System.out.println(Converter.viewCoinTable());
                        break;
                    case ("exit"):
                        exit = true;
                        updateValue.interrupt();
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
