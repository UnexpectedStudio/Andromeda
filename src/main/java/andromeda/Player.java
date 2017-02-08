package andromeda;

import java.util.List;

import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;

public class Player extends WebSocketImpl {
    public boolean isAuth = false;

    public Player(WebSocketListener listener, List<Draft> drafts) {
        super(listener, drafts);
    }

    public Player(WebSocketAdapter a, Draft d) {
        super(a, d);
    }
}
