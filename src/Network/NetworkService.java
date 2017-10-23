package Network;

import Models.RecommendedCodes;
import Models.RequestCode;
import com.google.common.base.Function;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.util.concurrent.TimeUnit;

/**
 * NetworkService is responsible to do
 * connection (websocket) with recommendation server
 */
public class NetworkService {

    private Socket socket;
    private String status;
    private long startTime;

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
//            IO.Options opts = new IO.Options();
//            opts.reconnection = true;
//            opts.reconnectionDelay = 1000;
//            opts.timeout = 25;
//            this.socket = IO.socket("http://apollo.gwachs.com:10443/code-recommendations");
            this.socket = IO.socket("http://0.0.0.0:10443/code-recommendations");
            Socket finalSocket = this.socket;
            socket.on(Socket.EVENT_CONNECT, args13 -> {
                System.out.println("CONECTADO");
                if (status.equals(disconnectedStatus)) {
                    status = connectedStatus;
                    startTime = System.currentTimeMillis();
                    System.out.println("GOING TO REQUEST");
                    Gson gson = new Gson();
                    Object request = gson.toJson(requestCode);
                    finalSocket.emit("getCodes", request);
                }
            }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                System.out.println("TIMEOUT SOCKET");
                errorFunction.run();
            }).on(Socket.EVENT_CONNECT_ERROR, args -> {
                System.out.println("EVENT_CONNECT_ERROR SOCKET");
//                errorFunction.run();
            }).on(Socket.EVENT_RECONNECT_FAILED, args -> {
                System.out.println("EVENT_RECONNECT_FAILED SOCKET");
//                errorFunction.run();
            }).on(Socket.EVENT_RECONNECT_ERROR, args -> {
                System.out.println("EVENT_RECONNECT_ERROR SOCKET");
//                errorFunction.run();
            }).on(Socket.EVENT_DISCONNECT, args12 -> {
//                cancelTimeout(errorFunction);
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

    private void cancelTimeout(Runnable function) {
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        if (TimeUnit.MILLISECONDS.toSeconds(totalTime) >= 20) {
            function.run();
        }
    }

    public void cancelEventDisconnecting(Runnable function) {
        status = disconnectedStatus;

        if (socket != null) {
            socket.disconnect();
        }

        socket = null;
        function.run();
    }
}
