package Network;

import Models.RecommendedCodes;
import Models.RequestCode;
import com.google.common.base.Function;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * NetworkService is responsible to do
 * connection (websocket) with recommendation server
 */
public class NetworkService {

    private Socket socket;
    private String status;

    static String connectedStatus = "CONNECTED";
    static String disconnectedStatus = "DISCONNECTED";

    public NetworkService() {
        status = disconnectedStatus;
    }

    public void getCodeRecommendation(RequestCode requestCode,
                                      Function<RecommendedCodes, Boolean> resultFunction,
                                      Runnable errorFunction) {
        System.out.println("GOING TO CONNECT");
        try {
//            this.socket = IO.socket("http://0.0.0.0:10443/code-recommendations");
            this.socket = IO.socket("https://quickslothrecommendationserver.herokuapp.com/code-recommendations");
            Socket finalSocket = this.socket;
            socket.on(Socket.EVENT_CONNECT, args13 -> {
                System.out.println("CONECTADO");
                if (status.equals(disconnectedStatus)) {
                    System.out.println("GOING TO REQUEST");
                    status = connectedStatus;
                    Gson gson = new Gson();
                    Object request = gson.toJson(requestCode);
                    finalSocket.emit("getCodes", request);
                }
            }).on(Socket.EVENT_DISCONNECT, args12 -> {
                System.out.println("DISCONNECT SOCKET");
            }).on("recommendationCodes", args -> {
                System.out.println("receive call recommendationCodes");
                status = disconnectedStatus;
                System.out.println((String) args[0]);
                Gson gson = new Gson();
                RecommendedCodes resultCodes  = gson.fromJson((String) args[0], RecommendedCodes.class);
                resultCodes.sortCodes();
                resultFunction.apply(resultCodes);
                finalSocket.disconnect();
            });

            socket.connect();
        } catch (Exception e1) {
            e1.printStackTrace();
            errorFunction.run();
        }
    }

    public void cancelEventDisconnecting(Runnable function) {
        status = disconnectedStatus;
        socket.disconnect();
        function.run();
    }
}
