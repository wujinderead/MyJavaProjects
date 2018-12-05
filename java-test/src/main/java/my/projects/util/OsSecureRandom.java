package my.projects.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * A Random implementation that uses random bytes sourced from the
 * operating system.
 */
public class OsSecureRandom extends Random implements Closeable {
    public static final Logger LOG =
            LoggerFactory.getLogger(OsSecureRandom.class);

    private static final long serialVersionUID = 6391500337172057900L;

    private final int RESERVOIR_LENGTH = 32;

    private String randomDevPath;

    private transient FileInputStream stream;

    private final byte[] reservoir = new byte[RESERVOIR_LENGTH];

    private int pos = reservoir.length;

    private void fillReservoir(int min) {
        if (pos >= reservoir.length - min) {
            System.out.printf("pos: %d, r.len: %d, r.len-min: %d\n", pos, reservoir.length, reservoir.length - min);
            try {
                if (stream == null) {
                    stream = new FileInputStream(new File(randomDevPath));
                }
                readFully(stream, reservoir, 0, reservoir.length);
                System.out.println("reservoir: " + printHexBinary(reservoir));
            } catch (IOException e) {
                throw new RuntimeException("failed to fill reservoir", e);
            }
            pos = 0;
        }
    }

    public OsSecureRandom() {
    }

    synchronized public void setRandomDevPath(String path) {
        this.randomDevPath = path;
        close();
    }

    @Override
    synchronized public void nextBytes(byte[] bytes) {
        int off = 0;
        int n = 0;
        while (off < bytes.length) {
            fillReservoir(0);
            n = Math.min(bytes.length - off, reservoir.length - pos);
            System.out.printf("b.len: %d, off: %d, r.len: %d, pos: %d, b.len-off: %d, r.len-pos: %d\n",
                    bytes.length, off, reservoir.length, pos, bytes.length - off, reservoir.length - pos);
            System.arraycopy(reservoir, pos, bytes, off, n);
            System.out.println("buf: " + printHexBinary(bytes));
            off += n;
            pos += n;
        }
    }

    @Override
    synchronized protected int next(int nbits) {
        fillReservoir(4);
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n = ((n << 8) | (reservoir[pos++] & 0xff));
        }
        return n & (0xffffffff >> (32 - nbits));
    }

    @Override
    synchronized public void close() {
        if (stream != null) {
            cleanupWithLogger(LOG, stream);
            stream = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    // read 'len' bytes from 'in' to 'buf' with offset 'off'
    public static void readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
        int ret;
        for(int toRead = len; toRead > 0; off += ret) {
            ret = in.read(buf, off, toRead);
            if (ret < 0) {
                throw new IOException("Premature EOF from inputStream");
            }
            toRead -= ret;
        }
    }

    public static void cleanupWithLogger(Logger logger,
                                         java.io.Closeable... closeables) {
        for (java.io.Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Throwable e) {
                    if (logger != null) {
                        logger.debug("Exception in closing {}", c, e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("/dev/urandom");
        OsSecureRandom random = new OsSecureRandom();
        random.setRandomDevPath("/dev/urandom");
        byte[] buf;
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[9];random.nextBytes(buf);System.out.println();
        buf = new byte[38];random.nextBytes(buf);System.out.println();
    }
}
