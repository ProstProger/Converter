import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TelegramBot extends TelegramLongPollingBot {
    private final static String token = System.getenv("TOKEN");
    private long chat_id;

    public static void main(String[] args) throws TelegramApiException {
        UpdateCurrencyValue updateValue = new UpdateCurrencyValue();
        updateValue.start();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBot());
    }

    @Override
    public String getBotUsername() {
        return "Cur_Convert_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            chat_id = update.getMessage().getChatId();
            String[] line;
            String message_text = update.getMessage().getText();
            line = message_text.split(" ");

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
                    double result = Converter.makeConvertation(quantity, Converter.getAllCoins().get(convertCoin), Converter.getAllCoins().get(desireCoin));
                    send(String.format("Итого: %.2f %s равен %s %s \n", quantity, Converter.getAllCoins().get(convertCoin).getName(), String.format("%.2f", result), Converter.getAllCoins().get(desireCoin).getName()));
                } else
                    send(Constants.CUR_NOT_EXIST);
            } else if ("view".equals(line[0])) {
                send(Converter.viewCoinTable());
            } else if ("/start".equals(line[0])) {
                send(Constants.START_MESSAGE);
            }else {
                send(Constants.COMMAND_NOT_EXIST);
            }
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
}

