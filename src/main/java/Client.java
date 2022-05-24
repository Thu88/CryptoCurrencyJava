import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    static Session mainSession;
    public static void main(String[] args) {
        try {
            List<Coin> coinList = SessionDataNavigator.initiateTradeHandlers();

            TimeUnit.SECONDS.sleep(10);

            Map<String, Object> subscribe = new HashMap<>();
            Map<String, Object> unsubscribe = new HashMap<>();

            List<String> params = new ArrayList<>();

            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    System.out.println(throwable.getMessage());
                    throwable.printStackTrace();
                    System.out.println(throwable.getLocalizedMessage());
                }
            });

            for (Coin coin : coinList) {
                if (!coin.getName().equals("ethbtc")) {
                    params.add(coin.getName() + "@trade");

                }
            }

            subscribe.put("method", "SUBSCRIBE");
            subscribe.put("params", params);
            subscribe.put("id", 3);

            unsubscribe.put("method", "UNSUBSCRIBE");
            unsubscribe.put("params", params);
            unsubscribe.put("id", 4);

            ClientManager clientManager = ClientManager.createClient();

            clientManager.getProperties().put("org.glassfish.tyrus.incomingBufferSize", 17000000);
            System.out.println(clientManager.getProperties());
            Session session = clientManager.connectToServer(TestClientEndpoint.class,
                    new URI("wss://stream.binance.com:9443/ws/linkbtc@trade"));
            mainSession = session;
            TimeUnit.SECONDS.sleep(10);

            System.out.println("Session open = " + session.isOpen());

            JsonObject jsonObjectSubscribe = Json.createObjectBuilder(subscribe).build();
            JsonObject jsonObjectUnsubscribe = Json.createObjectBuilder(unsubscribe).build();


            session.getBasicRemote().sendText(jsonObjectSubscribe.toString());

            Scanner scanner = new Scanner(System.in);
            System.out.println("Press enter to unsubscribe:");
            scanner.nextLine();
            System.out.println("Session before close. Session open = " + session.isOpen());
            session.getBasicRemote().sendText(jsonObjectUnsubscribe.toString());

            System.out.println("Press enter to exit:");
            scanner.nextLine();

            session.close();

            System.out.println("Session open = " + session.isOpen());
        }
        catch (InterruptedException inE)
        {
            inE.printStackTrace();
            System.exit(1);
        }
        catch (URISyntaxException uriE)
        {
            uriE.printStackTrace();
            System.exit(1);
        }
        catch (IOException ioE)
        {
            ioE.printStackTrace();
            System.exit(1);
        }
        catch (DeploymentException deployE)
        {
            deployE.printStackTrace();
            System.exit(1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
            System.out.println(e.getLocalizedMessage());
        }
    }
}
