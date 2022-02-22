import java.io.*;

public class Cli {
    private static final UpdateCurrencyValue updateValue = new UpdateCurrencyValue();

    public static void main(String[] args) {
        updateValue.start();
        cli();
    }

    public static void cli() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] line = null;
        boolean exit = false;

        System.out.println(Constants.START_MESSAGE);
        while (!exit) {
            try {
                line = reader.readLine().split(" ");

            } catch (IOException e) {
                System.out.println(Constants.IO_ERROR);
                e.printStackTrace();
            }
            if (line != null) {
                if (line.length == 3) {
                    double quantity;
                    try {
                        quantity = Double.parseDouble(line[0]);
                    } catch (NumberFormatException e) {
                        System.out.println(Constants.NOT_NUMBER_ERROR);
                        continue;
                    }
                    String convertCoin = line[1].toUpperCase();
                    String desireCoin = line[2].toUpperCase();

                    if (Converter.getAllCoins().containsKey(convertCoin) && Converter.getAllCoins().containsKey(desireCoin)) {
                        double result = Converter.makeConvertation(quantity, Converter.getAllCoins().get(convertCoin), Converter.getAllCoins().get(desireCoin));
                        System.out.printf("Итого: %.2f %s равен %s %s \n",
                                quantity,
                                Converter.getAllCoins().get(convertCoin).getName(),
                                String.format("%.2f", result),
                                Converter.getAllCoins().get(desireCoin).getName());
                    } else
                        System.out.println(Constants.CUR_NOT_EXIST);
                } else switch (line[0]) {
                    case ("view"):
                        System.out.println(Converter.viewCoinTable());
                        break;
                    case ("exit"):
                        exit = true;
                        updateValue.interrupt();
                        break;
                    default:
                        System.out.println(Constants.COMMAND_NOT_EXIST);
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
