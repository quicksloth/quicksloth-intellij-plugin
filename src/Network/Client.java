package Network;

import Models.RecommendedCodes;
import Models.RequestCode;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class Client extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("GOING TO CONNECT");
        Socket socket = null;
//        TODO: temporary.. improve all this stuff
        try {
            socket = IO.socket("http://0.0.0.0:10443/code-recommendations");
            Socket finalSocket = socket;
            socket.on(Socket.EVENT_CONNECT, args13 -> {
                System.out.println("CONECTADO");
                RequestCode rc = new RequestCode("read file",
                        "Python",
                        Collections.singletonList("os"),
                        Collections.singletonList("open file to read file"));

                Gson gson = new Gson();
                Object request = gson.toJson(rc);
                finalSocket.emit("getCodes", request);
            }).on(Socket.EVENT_DISCONNECT, args12 -> {
                System.out.println("DISCONNECT SOCKET");
            }).on("recommendationCodes", args -> {
                System.out.println("receive call recommendationCodes");
                System.out.println((String) args[0]);
                Gson gson = new Gson();
                RecommendedCodes resultCodes  = gson.fromJson((String) args[0], RecommendedCodes.class);
                finalSocket.disconnect();
            });

            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}
