import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Date;


public class TelegramBot extends TelegramLongPollingBot {
    private final static String TOKEN = System.getenv("TOKEN");
    private static Date lastDateUpdate = new Date();
    private long chatId;

    public static void main(String[] args) throws TelegramApiException {
        Converter.updateCurrencyValue();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBot());
    }

    @Override
    public String getBotUsername() {
        return "Cur_Convert_bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String[] line;
            String messageText = update.getMessage().getText();
            line = messageText.split(" ");

            if (line.length == 3) {
                double quantity = 0.0;
                try {
                    quantity = Double.parseDouble(line[0]);
                } catch (NumberFormatException e) {
                    send(Constants.NOT_NUMBER_ERROR);
                }
                String convertCoin = line[1].toUpperCase();
                String desireCoin = line[2].toUpperCase();
                if (Converter.getAllCoins().containsKey(convertCoin) && Converter.getAllCoins().containsKey(desireCoin)) {
                    checkDateUpdate();
                    double result = Converter.makeConvertation(quantity, Converter.getAllCoins().get(convertCoin), Converter.getAllCoins().get(desireCoin));
                    send(String.format("Итого: %.2f %s равен %s %s \n", quantity, Converter.getAllCoins().get(convertCoin).getName(), String.format("%.2f", result), Converter.getAllCoins().get(desireCoin).getName()));
                } else
                    send(Constants.CUR_NOT_EXIST);
            } else if ("view".equals(line[0])) {
                send(Converter.viewCoinTable());
            } else if ("/start".equals(line[0])) {
                send(Constants.START_MESSAGE_TELEGRAM);
            } else {
                send(Constants.COMMAND_NOT_EXIST);
            }
        }
    }

    private void send(String textMessage) {
        SendMessage message = SendMessage
                .builder()
                .chatId(Long.toString(chatId))
                .text(textMessage)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void checkDateUpdate() {
        Date date = new Date();

        if ((date.getTime() - lastDateUpdate.getTime()) > Constants.UPDATE_MAP_MILLIS) {
            Converter.updateCurrencyValue();
            lastDateUpdate = date;
        }
    }
}

