package Network;

import Models.RecommendedCodes;
import Models.RequestCode;
import com.google.common.base.Function;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

/**
 * NetworkService is responsible to do
 * connection (websocket) with recommendation server
 */
public class NetworkService {

    public Socket socket;

    public NetworkService() {
    }

    public void getCodeRecommendation(RequestCode requestCode, Function<RecommendedCodes, Boolean> function) {
        System.out.println("GOING TO CONNECT");
        try {
            this.socket = IO.socket("http://0.0.0.0:10443/code-recommendations");
            Socket finalSocket = this.socket;
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
                resultCodes.sortCodes();
                function.apply(resultCodes);
                finalSocket.disconnect();
            });

            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    public void cancelEventDisconnecting(Runnable function) {
        socket.disconnect();
        function.run();
    }
}
