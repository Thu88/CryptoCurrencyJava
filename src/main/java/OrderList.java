import javax.json.JsonObject;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class OrderList {
    String coinName;
    long lastUpdateId;
    List<Order> buyOrders;
    List<Order> sellOrders;
    boolean empty;
    boolean hasBought;
    Timestamp buyTime;
    double buyPrice;
    double amount;
    Timestamp sellTime;
    double sellPrice;
    double btc = 0.005;
    boolean snapshotDownloaded;


    public OrderList(long lastUpdateId, List<Order> buyOrders, List<Order> sellOrders) {
        this.lastUpdateId = lastUpdateId;
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
        empty = true;
        hasBought = false;
        amount = 0;
    }

    public OrderList(String symbol)
    {
        buyOrders = new ArrayList<>();
        sellOrders = new ArrayList<>();
        lastUpdateId = 0;
        coinName = symbol;
        empty = true;
        hasBought = false;
        amount = 0;
        snapshotDownloaded = false;
    }

    public void modifyBuyOrders(List<Order> orders)
    {
        for (Order newOrder : orders)
        {
            boolean orderModified = false;
            for (Order oldOrder : buyOrders)
            {
                if (newOrder.getPrice() == oldOrder.getPrice())
                {
                    if (newOrder.getQuantity() == 0) {
                        buyOrders.remove(oldOrder);
                        break;
                    }
                    oldOrder.setQuantity(newOrder.getQuantity());
                    orderModified = true;
                    break;
                }
            }
            if(!orderModified)
            {
                if (newOrder.getQuantity() > 0)
                    buyOrders.add(newOrder);
            }
        }
    }

    public void modifySellOrders(List<Order> orders)
    {
        for (Order newOrder : orders)
        {
            boolean orderModified = false;
            for (Order oldOrder : sellOrders)
            {
                if (newOrder.getPrice() == oldOrder.getPrice())
                {
                    if (newOrder.getQuantity() == 0) {
                        sellOrders.remove(oldOrder);
                        break;
                    }
                    oldOrder.setQuantity(newOrder.getQuantity());
                    orderModified = true;
                    break;
                }
            }
            if(!orderModified)
            {
                if (newOrder.getQuantity() > 0)
                    sellOrders.add(newOrder);
            }
        }
    }

    public void modifyAllOrdersFromStream (JsonObject data)
    {
        OrderList orderList = JSONUtil.decodeOrders(data);
        modifyBuyOrders(orderList.getBuyOrders());
        modifySellOrders(orderList.getSellOrders());

        if (empty) {
            if (buyOrders.size() > 0 || sellOrders.size() > 0) {
                empty = false;
            }
        }

        if (!snapshotDownloaded)
        {
            modifyAllOrdersFromSnapshot(OrderBook.getOrderList(coinName));
            buy(btc);
            snapshotDownloaded = true;
        }

        sell();
    }

    public void modifyAllOrdersFromSnapshot (OrderList orderList)
    {
        lastUpdateId = orderList.getLastUpdateId();

        for (Iterator<Order> iterator = buyOrders.iterator();  iterator.hasNext();)
        {
            Order order = iterator.next();
            if (order.getFinalUpdateIDinEvent() <= lastUpdateId)
            {
                iterator.remove();
            }
        }

        for (Iterator<Order> iterator = sellOrders.iterator();  iterator.hasNext();)
        {
            Order order = iterator.next();
            if (order.getFinalUpdateIDinEvent() <= lastUpdateId)
            {
                iterator.remove();
            }
        }

        modifyBuyOrders(orderList.getBuyOrders());
        modifySellOrders(orderList.getSellOrders());
    }

    public void buy(double btc)
    {
        if (!hasBought)
        {
            Collections.sort(buyOrders);
            Collections.sort(sellOrders);
            int i;

            i = sellOrders.size()-1;
            while (true)
            {
                if (sellOrders.get(i).total() > btc)
                {
                    Toolkit.getDefaultToolkit().beep();
                    System.out.println("Buying " + coinName + ", at price: " + sellOrders.get(i).getPrice() );
                    buyTime = new Timestamp(System.currentTimeMillis());
                    buyPrice = sellOrders.get(i).getPrice();
                    amount = btc / buyPrice; //Changed by dividing with buy price instead of multiplaying which gives a wrong result.
                    hasBought = true;
                    return;
                }
                i--;
            }

        }
    }

    public void sell()
    {
        if (hasBought)
        {
            Collections.sort(buyOrders);
            Collections.sort(sellOrders);
            if (buyOrders.get(0).getPrice() < buyPrice * 0.98)
            {
                if (buyOrders.get(0).quantity > amount) {
                    Toolkit.getDefaultToolkit().beep();
                    sellTime = new Timestamp(System.currentTimeMillis());
                    sellPrice = buyOrders.get(0).getPrice();
                    OrderBook.unfollowOrderBook(this);
                    Wallet.saveTrade(this);
                    return;
                }
            }
            for (Order order : buyOrders)
            {
                if (order.getPrice() > buyPrice * 1.05) {
                    if (order.quantity > amount) {
                        Toolkit.getDefaultToolkit().beep();
                        sellTime = new Timestamp(System.currentTimeMillis());
                        sellPrice = order.getPrice();
                        OrderBook.unfollowOrderBook(this);
                        Wallet.saveTrade(this);
                        return;
                    }
                }
            }
        }
    }



    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public long getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public List<Order> getBuyOrders() {
        return buyOrders;
    }

    public void setBuyOrders(List<Order> buyOrders) {
        this.buyOrders = buyOrders;
    }

    public List<Order> getSellOrders() {
        return sellOrders;
    }

    public void setSellOrders(List<Order> sellOrders) {
        this.sellOrders = sellOrders;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String toString()
    {
        /*Collections.sort(buyOrders);
        Collections.sort(sellOrders);
        String text = "";
        for (Order order: sellOrders)
        {
            text += order.toString();
            text += "\n";
        }

        text += "\n";

        for (Order order : buyOrders)
        {
            text += order.toString();
            text += "\n";
        }*/

        String text = "";

        Collections.sort(buyOrders);
        Collections.sort(sellOrders);

        for (int i = sellOrders.size() - 1 , j = 5; i >= 0 && j >= 0; i--, j--)
        {
            text += sellOrders.get(i).toString();
            text += "\n";
        }

        text += "\n";

        for (int i = 0, j = 5; i < buyOrders.size() && j >= 0; i++, j--)
        {
            text += buyOrders.get(i).toString();
            text += "\n";
        }



        return text;
    }
}
