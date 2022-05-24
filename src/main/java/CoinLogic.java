import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinLogic {

    private List<TradeVolume> volumeList;
    private Coin coin;
    private boolean ongoinAlarm;

    public CoinLogic(Coin coin)
    {
        this.coin = coin;
        volumeList = new ArrayList<>();
        volumeList.add(new TradeVolume(0,0,0, new Timestamp(System.currentTimeMillis())));
        ongoinAlarm = false;
    }

    public void addTradeToVolumeList(Trade trade)
    {
        TradeVolume lastVolumeForBugReport = volumeList.get(volumeList.size()-1);


        while (true)
        {
            if (volumeList.size() > 2880)
            {
                int size = volumeList.size() - 1;
                System.out.println("-------");
                System.out.println("Volumelist bigger than 500");
                System.out.println(coin.getName());
                System.out.println("Volume "+ (size - 5) + ": "+ volumeList.get(size - 5));
                System.out.println("Volume "+ (size - 5) + ": "+ volumeList.get(size - 4));
                System.out.println("Volume "+ (size - 5) + ": "+ volumeList.get(size - 3));
                System.out.println("Volume "+ (size - 5) + ": "+ volumeList.get(size - 2));
                System.out.println("Volume "+ (size - 5) + ": "+ volumeList.get(size - 1));
                System.out.println("LastVolume "+ (size - 5) + ": "+ volumeList.get(size));
                System.out.println("Last trade: " + trade);
                System.out.println("Last Volume for bug report: " + trade);
                System.out.println("-------");
                System.exit(1);
            }
            TradeVolume lastVolume = volumeList.get(volumeList.size()-1);

            if (lastVolume.isTradeInCurrentPeriod(trade))
            {
                lastVolume.addTrade(trade);
                break;
            }
            else
            {
                TradeVolume tradeVolume = new TradeVolume(0,0,0,new Timestamp(lastVolume.getEndTime().getTime()));
                volumeList.add(tradeVolume);
                ongoinAlarm = false;
            }
        }
    }

    public void alarm()
    {
        List<TradeVolume> last5vols;
        List<TradeVolume> allExcludingLast5vols;
        double last5avrRealVol;
        double allExcludingLast5avrRealVol;
        double last5avrTotalVol;
        double allExcludingLast5avrTotalVol;

        last5avrRealVol = 0;
        allExcludingLast5avrRealVol = 0;
        last5avrTotalVol = 0;
        allExcludingLast5avrTotalVol = 0;

        if (volumeList.size() > 60)
        {
            last5vols = volumeList.subList(volumeList.size() - 5, volumeList.size());
            allExcludingLast5vols = volumeList.subList(0, volumeList.size() - 5);

            for (TradeVolume tradeVolume : last5vols)
            {
                last5avrRealVol += tradeVolume.getRealVolume();
                last5avrTotalVol += tradeVolume.getTotalVol();
            }

            last5avrRealVol = last5avrRealVol / last5vols.size();
            last5avrTotalVol = last5avrTotalVol / last5vols.size();

            /*for (TradeVolume tradeVolume : allExcludingLast5vols)
            {
                allExcludingLast5avrRealVol += tradeVolume.getRealVolume();
                allExcludingLast5avrTotalVol += tradeVolume.getTotalVol();
            }*/

            for (int i = allExcludingLast5vols.size() - 31; i < allExcludingLast5vols.size(); i++)
            {
                allExcludingLast5avrRealVol += allExcludingLast5vols.get(i).getRealVolume();
                allExcludingLast5avrTotalVol += allExcludingLast5vols.get(i).getTotalVol();
            }

            allExcludingLast5avrRealVol += allExcludingLast5avrRealVol / allExcludingLast5vols.size();
            allExcludingLast5avrTotalVol += allExcludingLast5avrTotalVol / allExcludingLast5vols.size();

            if (last5avrTotalVol > (allExcludingLast5avrTotalVol * 3)) {
                if (allExcludingLast5avrRealVol < 0) {
                    if (last5avrRealVol > 1) {
                        printAlarm(last5vols);
                    }
                } else {
                    if (last5avrRealVol - allExcludingLast5avrRealVol > 1) {
                        printAlarm(last5vols);
                    }
                }
            }

        }
    }

    public void printAlarm(List<TradeVolume> last5)
    {
        if (!ongoinAlarm)
        {
            List<TradeVolume> last5rightOrder = new ArrayList<>(last5);
            Collections.reverse(last5rightOrder);

            System.out.println("Alarm: " + coin.getName() + " Time: " + new Timestamp(System.currentTimeMillis()));
            System.out.print("Last 5 vols: ");
            for (TradeVolume tradeVolume : last5rightOrder)
            {
                System.out.print(" Vol: " + tradeVolume.getRealVolume() + " time: " + tradeVolume.getStartTime());
            }
            System.out.println();
            ongoinAlarm = true;
            volumeList = volumeList.subList(volumeList.size()-1, volumeList.size());
            OrderBook.followOrderBook(coin.getName());
        }
    }
}
