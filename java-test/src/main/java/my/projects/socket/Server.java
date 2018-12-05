package my.projects.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    int port;
    AtomicBoolean running = new AtomicBoolean(true);
    public Server(int port) {
        this.port = port;
    }
    public void start() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            Runtime.getRuntime().addShutdownHook(new Thread(
                    () -> running.set(false)
            ));
            while (running.get()) {
                Socket socket = server.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) {
        Server server = new Server(12345);
        server.start();
    }
}

class ServerHandler implements Runnable {
    private Socket socket;
    public ServerHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            while (socket != null && socket.isConnected()) {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), false);
                char[] buf = new char[10];
                int readed;
                out.write("respond to msg ");
                readed = in.read(buf);
                out.write(buf, 0, readed);
                System.out.println("get msg:"+ new String(buf));
                out.flush();
                Thread.sleep(100);
            }
            System.out.println(Thread.currentThread().getName() + ": handler exit!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}

/*
{
  "name=oraclexs
  "config": {
    connector.class=cn.enn.com.kafka.connect.cdc.xstream.OracleSourceConnector
    initial.database=EE.oracle.docker
    password=xstrmadmin
    server.name=10.19.138.135
    tasks.max=1
    server.port=11521
    name=oraclexs
    is_traced=false
    oracle.xstream.server.names=xoutt
    topicFormat.format=xstream_out
    username=xstrmadmin
    oracle.xstream.batch.interval=1"
  }
}
 */
