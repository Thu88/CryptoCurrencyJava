import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Wallet {

    public static void saveTrade(OrderList orderList)
    {
        try {
            File file = new File("/home/thomas/Projects2/trades.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, true);
            String s = String.format("Symbol: %s, buy time: %s, buy price %.12f, amount bought: %.12f, sell time %s, sell price %.12f, gain: %.12f\n",
                    orderList.getCoinName(), orderList.buyTime, orderList.buyPrice, orderList.amount, orderList.sellTime, orderList.sellPrice,
                    (orderList.sellPrice * orderList.amount) - (orderList.buyPrice * orderList.amount));
            fileWriter.append(s);
            fileWriter.close();
            System.out.println(s);
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }
}
