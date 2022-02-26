import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class UpdateCurrencyValueCli extends Thread{

    @Override
    public void run() {
        while(!isInterrupted()){
            Converter.updateCurrencyValue();
            try {
                Thread.sleep(43200000); //Update allCoins map every 12 hours
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
