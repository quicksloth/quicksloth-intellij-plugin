package Network;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class Client extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.print("GOING TO CONNECT");
        Socket socket = null;
        try {
            socket = IO.socket("http://http://0.0.0.0:6060");
            Socket finalSocket = socket;
            socket.on(Socket.EVENT_CONNECT, args13 -> {
                System.out.print("CONECTADO");
                finalSocket.emit("foo", "hi");
                finalSocket.disconnect();
            }).on("event", args1 -> {
                System.out.print("EVENT");
            }).on(Socket.EVENT_DISCONNECT, args12 -> {});
            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}
