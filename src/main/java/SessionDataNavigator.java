import javax.json.JsonObject;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SessionDataNavigator {

    private static List<TradeHandler> tradeHandlers;

    public static void createTrade(JsonObject jsonObject)
    {
        Trade trade = new Trade(jsonObject);

        tradeHandlers.get(findTradeHandler(trade.getSymbol())).incomingTrade(trade);
    }

    public static List<Coin> initiateTradeHandlers()
    {
        List<Coin> coinList = Startup.createCoinTables();

        tradeHandlers = new ArrayList<>();

        for (Coin coin : coinList)
        {
            if (!coin.getName().equals("ethbtc"))
            {
                coin.setName(coin.getName().toLowerCase());
                tradeHandlers.add(new TradeHandler(coin));
            }
        }

        return coinList;
    }

    public static int findTradeHandler(String symbol)
    {
        for (int i = 0; i < tradeHandlers.size(); i++)
        {
            if (tradeHandlers.get(i).getCoin().getName().equals(symbol))
            {
                return i;
            }
        }

        return -1;
    }
}
