package Network;

import Models.RecommendedCodes;
import Models.RequestCode;
import View.MainToolWindowFactory;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.Collections;

/**
 * NetworkService is responsible to do
 * connection (websocket) with recommendation server
 */
public class NetworkService extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        RequestCode rc = new RequestCode("read file",
                "Python",
                Collections.singletonList("os"),
                Collections.singletonList("open file to read file"));
        MainToolWindowFactory toolWindowFactory = new MainToolWindowFactory();
        getCodeRecommendation(rc, toolWindowFactory);
    }

    static public void getCodeRecommendation(RequestCode requestCode, MainToolWindowFactory toolwindow) {
        System.out.println("GOING TO CONNECT");
        Socket socket = null;
        try {
            socket = IO.socket("http://0.0.0.0:10443/code-recommendations");
            Socket finalSocket = socket;
            socket.on(Socket.EVENT_CONNECT, args13 -> {
                System.out.println("CONECTADO");
                Gson gson = new Gson();
                Object request = gson.toJson(requestCode);
                finalSocket.emit("getCodes", request);
            }).on(Socket.EVENT_DISCONNECT, args12 -> {
                System.out.println("DISCONNECT SOCKET");
            }).on("recommendationCodes", args -> {
                System.out.println("receive call recommendationCodes");
                System.out.println((String) args[0]);
                Gson gson = new Gson();
                RecommendedCodes resultCodes  = gson.fromJson((String) args[0], RecommendedCodes.class);
                toolwindow.showResults(resultCodes);
//                return resultCodes;
                finalSocket.disconnect();
            });

            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}
