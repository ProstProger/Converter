import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class TelegramBot extends TelegramLongPollingBot {
    private long chat_id;
    private boolean startDescription = true;


    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBot());
    }



    @Override
    public String getBotUsername() {
        return "Cur_Convert_bot";
    }

    @Override
    public String getBotToken() {
        return "5102970222:AAEJVnGtuDFFxVc7Nd6JqpYxU41iMoNExwE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        chat_id = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (startDescription) {
                send("Добро пожаловать в конвертер валют. " +
                        "\nДля просмотра кодов наберите \"view\"\n");
                send("Введите количество и из какой валюты в какую хотите перевести в формате\n" +
                        "[количество] [код конвертируемой валюты] [код требуемой валюты]:\n" +
                        "Например: 1000 usd rub");
                startDescription = false;
            }
            String responseData = Converter.makeRequest();
            Document document = Jsoup.parse(responseData, "", Parser.xmlParser());
            Map<String, Coin> allCoin = Converter.createCoinMap(document);
            String[] line = null;
            boolean exit = false;


            String message_text = update.getMessage().getText();
            line = message_text.split(" ");

            if (line.length == 3) {
                double quantity = 0.0;
                try {
                    quantity = Double.parseDouble(line[0]);
                } catch (NumberFormatException e) {
                    send("Первым параметром введено не число, повторите ввод.");
                }
                String convertCoin = line[1].toUpperCase();
                String desireCoin = line[2].toUpperCase();
                if (allCoin.containsKey(convertCoin) && allCoin.containsKey(desireCoin)) {
                    double result = Converter.makeConvertation(quantity, allCoin.get(convertCoin), allCoin.get(desireCoin));
                    send(String.format("Итого: %.2f %s равен %s %s \n", quantity, allCoin.get(convertCoin).getName(), String.format("%.2f", result), allCoin.get(desireCoin).getName()));
                } else
                    send("Введен неправильный формат, либо такой валюты не существует.\nДля просмотра кодов наберите \"view\"");
            } else if ("view".equals(line[0])) {
                viewCoinTable(allCoin);
            } else {
                send("Такой команды нет.");
            }
            line = null;
        }
    }

    public void send(String textMessage) {
        SendMessage message = SendMessage
                .builder()
                .chatId(Long.toString(chat_id))
                .text(textMessage)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void viewCoinTable(Map<String, Coin> coinMap) {
        ArrayList<String> keyList = new ArrayList<>();
        Stream<Map.Entry<String, Coin>> stream = coinMap.entrySet().stream();
        stream.sorted(Comparator.comparing(e -> e.getValue().getName())).forEach(e -> keyList.add(e.getValue().getCharCode()));

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : keyList) {
            stringBuilder.append(String.format("%s - %s", coinMap.get(s).getCharCode(), coinMap.get(s).getName())).append("\n");
        }
        send(stringBuilder.toString());
    }
}

