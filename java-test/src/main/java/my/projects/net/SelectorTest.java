package my.projects.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SelectorTest {
    public static void main(String[] args) throws Exception {
        testSelector();
    }

    private static void testSelector() throws IOException {
        Selector nioSelector = Selector.open();
        ServerSocketChannel serverChannel = openServerSocket("localhost", 9092);
        AtomicBoolean isRunning = new AtomicBoolean(true);
        CountDownLatch latch = new CountDownLatch(1);
        run(isRunning, latch, nioSelector, serverChannel);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isRunning.set(false);
            System.out.println("wait latch.");
            try {
                latch.await();
            } catch (InterruptedException e) {
                // ignore
            }
            System.out.println("wait latch over");
        }));
    }

    private static void run(AtomicBoolean isRunning, CountDownLatch latch, Selector nioSelector, ServerSocketChannel serverChannel) throws IOException {
        serverChannel.register(nioSelector, SelectionKey.OP_ACCEPT);
        try {
            while (isRunning.get()) {
                try {
                    int ready = nioSelector.select(500);
                    if (ready > 0) {
                        Set<SelectionKey> keys = nioSelector.selectedKeys();
                        Iterator<SelectionKey> iter = keys.iterator();
                        while (iter.hasNext() && isRunning.get()) {
                            try {
                                SelectionKey key = iter.next();
                                iter.remove();
                                if (key.isAcceptable())
                                    accept(key);
                                else
                                    throw new IllegalStateException("Unrecognized key state for acceptor thread.");
                            } catch (Exception e) {
                                System.err.println("Error while accepting connection" + e);
                            }
                        }
                    }
                }
                catch (IOException e) {
                    throw e;
                }
            }
        } finally {
            try {
                serverChannel.close();
                System.out.println("serverChannel closed.");
            } catch (Exception e) {
                // ignore
            }
            try {
                nioSelector.close();
                System.out.println("selector closed.");
            } catch (Exception e) {
                // ignore
            }
            latch.countDown();
        }
    }

    private static void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        System.out.println("server addr: " + serverSocketChannel.getLocalAddress());
        SocketChannel socketChannel = serverSocketChannel.accept();
        try {
            socketChannel.configureBlocking(false);
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setKeepAlive(true);

            System.out.printf("Accepted connection from %s on %s and assigned it to processor %d, sendBufferSize [actual|requested]: [%d|%d] recvBufferSize [actual|requested]: [%d|%d]\n", socketChannel.socket().getRemoteSocketAddress(), socketChannel.socket().getLocalSocketAddress(), 1,
                    socketChannel.socket().getSendBufferSize(), 0,
                    socketChannel.socket().getReceiveBufferSize(), 0);

            System.out.println("local addr: " + socketChannel.getLocalAddress());
            System.out.println("remote addr: " + socketChannel.getRemoteAddress());
            ByteBuffer buf = ByteBuffer.allocate(512);
            int n = socketChannel.read(buf);
            String received = new String(buf.array(), 0, n);
            System.out.println("readed: " + received);

            ByteBuffer writeBuf = ByteBuffer.allocate(512);
            writeBuf.put("hahahah".getBytes());
            socketChannel.write(writeBuf);
        } catch (Exception e) {
            socketChannel.close();
        } finally {
            socketChannel.close();
        }
    }

    private static ServerSocketChannel openServerSocket(String host, int port) throws IOException {
        InetSocketAddress socketAddress = (host == null || host.trim().equals("")) ?
                new InetSocketAddress(port) : new InetSocketAddress(host, port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        try {
            serverChannel.socket().bind(socketAddress);
            System.out.printf("Awaiting socket connections on %s:%d.\n", socketAddress.getHostString(), serverChannel.socket().getLocalPort());
        } catch (SocketException e) {
            throw new IOException(String.format("Socket server failed to bind to %s:%d: %s.", socketAddress.getHostString(), port, e.getMessage()), e);
        }
        return serverChannel;
    }
}
