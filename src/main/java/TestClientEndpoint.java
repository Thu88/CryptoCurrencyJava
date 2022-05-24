import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;

@ClientEndpoint
public class TestClientEndpoint {

    public TestClientEndpoint()
    {
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                System.out.println(throwable.getMessage());
                throwable.printStackTrace();
                System.out.println(throwable.getLocalizedMessage());
            }
        });
    }
    @OnMessage
    public void onMessage(String message, Session session)
    {
        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject jsonObject = jsonReader.readObject();
        if (jsonObject.containsKey("id"))
        {
            System.out.println(jsonObject);
            return;
        }

        if (jsonObject.containsKey("code"))
        {
            System.out.println(jsonObject);
            System.exit(1);
        }

        if (jsonObject.containsKey("e"))
        {
            if (jsonObject.getString("e").equals("trade"))
            {
                SessionDataNavigator.createTrade(jsonObject);
            }
            if (jsonObject.getString("e").equals("depthUpdate"))
            {
                OrderBook.incomingOrder(jsonObject);
            }

        }
    }

    @OnError
    public void onError(Session session, Throwable t)
    {
        System.out.println("Closing connection.");
        t.printStackTrace();
        try {
            session.close();
        }
        catch (Exception e)
        {
            System.out.println("Couldnt close connection");
            e.printStackTrace();
        }
        System.exit(1);
    }
}
