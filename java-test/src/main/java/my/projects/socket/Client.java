package my.projects.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    int port;
    String host;
    AtomicBoolean running = new AtomicBoolean(true);
    public Client(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        Socket socket = null;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        Scanner scanner = new Scanner(System.in);
        BufferedReader br = null;
        BufferedWriter writer = null;
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(
                    () -> running.set(false)
            ));
            socket = new Socket(host, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (running.get() && !socket.isClosed()) {
                String in = stdin.readLine();
                if (in == null || in.length()==0) {
                    continue;
                }
                System.out.println("get input:" + in);
                writer.write(in);
                writer.flush();
                char[] buf = new char[10];
                int readed = br.read(buf);
                System.out.println("get respond:" + new String(buf));
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 12345).start();
    }

}
