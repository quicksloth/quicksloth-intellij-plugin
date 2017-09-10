package Network;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class Client extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.print("GOING TO CONNECT");
//        Socket socket = IO.socket("http://http://0.0.0.0:6060/code-recommendations");
//        socket.on(Socket.EVENT_CONNECT, args13 -> {
//            System.out.print("CONECTADO");
//            socket.emit("foo", "hi");
//            socket.disconnect();
//        }).on("event", args1 -> {
//            System.out.print("EVENT");
//        }).on(Socket.EVENT_DISCONNECT, args12 -> {});
//        socket.connect();
    }
}
