import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class TradeHandler {
    private List<Trade> tradeList;
    private Coin coin;
    private CoinLogic coinLogic;

    public TradeHandler(Coin coin) {
        this.coin = coin;
        tradeList = new ArrayList<>();
        coinLogic = new CoinLogic(coin);
    }

    public void incomingTrade(Trade trade)
    {
        tradeList.add(trade);

        coinLogic.addTradeToVolumeList(trade);
        coinLogic.alarm();

        if (tradeList.size() > 1000)
        {
            /*System.out.println("Saving " + coin.getName() + " to database. " + new Timestamp(System.currentTimeMillis()));
            saveToDatabase();*/
            tradeList.clear();
        }
    }

    public void saveToDatabase()
    {
        TradeDatabaseObject tradeDatabaseObject = new TradeDatabaseObject();
        tradeDatabaseObject.createAll(tradeList);
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }
}
