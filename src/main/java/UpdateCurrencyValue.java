import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

class UpdateCurrencyValue extends Thread{

    @Override
    public void run() {
        while(!isInterrupted()){
            String responseData = Converter.makeRequest();
            Document document = Jsoup.parse(responseData, "", Parser.xmlParser());
            Converter.createCoinMap(document);
            try {
                Thread.sleep(43200000); //Update allCoins map every 12 hours
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
