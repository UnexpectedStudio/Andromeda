package andromeda;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import andromeda.config.Config;
import andromeda.config.JsonConfiguration;
import andromeda.server.Server;

public class Main {
    private static File configFile;
    public static Config config = new Config();
    
    public static void main(String[] args)
            throws Exception {
        Server s = new Server(new InetSocketAddress(42042));

        System.out.println("Starting server...");
        s.start();
        
        configFile = new File("config.json");
        reload();
        boolean running = true;
        while (running) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String str = br.readLine();
            if (str.equalsIgnoreCase("stop")) running = false;
            else if (str.equalsIgnoreCase("reload")) {
                try {
                    reload();
                } catch (Exception e) {
                    System.out.println("CANNOT RELOAD.");
                    e.printStackTrace();
                }
            } else if (str.startsWith("msg")) {
                try {
                    s.sendToAll(str.substring(4));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Stopping in 5s...");
        Thread.sleep(5000);
        s.stop();
        System.out.println("Stopped.");
    }
    
    private static void reload() throws Exception {
        System.out.println("Reloading...");
        synchronized(config) {
            if (!configFile.exists()) {
                configFile.createNewFile();
                config.save(configFile);
            }
            config = JsonConfiguration.load(configFile, Config.class);
        }
        System.out.println("Reload ended.");
    }
}
