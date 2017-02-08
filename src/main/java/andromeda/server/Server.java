package andromeda.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import andromeda.Main;
import andromeda.Player;
import andromeda.question.Question;
import info.debatty.java.stringsimilarity.Levenshtein;

public class Server extends WebSocketServer {
    private static Server instance;
    private final Levenshtein tester = new Levenshtein();
    private static final Random rand = new Random();
    private final List<Player> mPlayers = new ArrayList<>();

    public Server(InetSocketAddress address) {
        super(address);
        setWebSocketFactory(new WebSocketServerFactory() {
            
            public SocketChannel wrapChannel(SocketChannel channel, SelectionKey key)
                    throws IOException {
                return (SocketChannel) channel;
            }
            
            public WebSocketImpl createWebSocket(WebSocketAdapter a,
                    List<Draft> d, Socket s) {
                return new Player(a, d);
            }
            
            public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d,
                    Socket s) {
                return new Player(a, d);
            }
        });
        instance = this;
    }
    
    public static Server getInstance() {
        return instance;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("<font color='red'>NEED AUTHENTICATION</font>");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason,
            boolean remote) {
        synchronized(mPlayers) {
            mPlayers.remove((Player)conn);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.equals("We are all alive.")) {
            ((Player)conn).isAuth = true;
            synchronized(mPlayers) {
                mPlayers.add((Player)conn);
            }
            conn.send("<font color='green'>AUTHENTICATION OK</font>");
            String toSend = Main.config.firstMessages[rand.nextInt(Main.config.firstMessages.length)];
            System.out.println("> " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + toSend);
            conn.send(toSend);
            return;
        }
        if (!((Player)conn).isAuth) {
            conn.send("<font color='red'>AUTHENTICATION ERROR</font>");
            return;
        }
        String toSend = null;
        System.out.println("< " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message);
        conn.send("<i>Vous> " + message + "</i>");
        synchronized(Main.config) {
            for (Question q : Main.config.questions) {
                double distance = tester.distance(q.question.toLowerCase(), message.toLowerCase());
                if (distance <= Main.config.levenshtein) {
                    toSend = q.anwsers[rand.nextInt(q.anwsers.length)];
                    break;
                }
            }
            if (toSend == null) toSend = Main.config.notFound[rand.nextInt(Main.config.notFound.length)];
            System.out.println("> " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + toSend);
            conn.send(toSend);
        }
    }
    
    public void sendToAll(String text) {
        synchronized(mPlayers) {
            for (Player p : mPlayers) {
                try {
                    p.send(text);
                } catch (Exception e) {}
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }
}
