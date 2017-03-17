package net.cupmouse.minecraft.streamproxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Main {
    private static Main instance;
    private boolean stop;
    private UpdateReceiver updateReceiver;
    private SocketChannel socket;

    public static void main(String[] args) {
        instance = new Main();

        try {
            instance.start(args[0], Integer.parseInt(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(String hostName, int port) throws IOException {
        updateReceiver = new UpdateReceiver(hostName, port);
        updateReceiver.start();
    }

    private void stop() {
        stop = true;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public class UpdateReceiver extends Thread {
        private final String hostName;
        private final int port;

        public UpdateReceiver(String hostName, int port) {
            this.hostName = hostName;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(hostName), port);
                System.out.println("CONNECTING: " + address);
                socket = SocketChannel.open();

                socket.socket().setSoTimeout(1000);

                if (!socket.connect(address)) {
                    if (!socket.finishConnect()) {
                        throw new IOException("接続確立できない");
                    }
                }

                ByteBuffer magic = ByteBuffer.wrap(new byte[] {(byte) 246, (byte) 129, (byte) 155, 116, (byte) 128});
                socket.write(magic);
                ByteBuffer magic_reply = ByteBuffer.allocate(5);
                socket.read(magic_reply);
                magic_reply.position(0);
                if (!Arrays.equals(magic_reply.array(), new byte[]{105, (byte) 213, 79, 25, (byte) 180})) {
                    throw new IOException("ふさわしくない相手");
                }
                socket.write(ByteBuffer.wrap(new byte[]{100}));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            System.out.println("CONNECTION ESTABLISHED, START RECEIVING MESSAGE.");

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            try {
                while (!stop) {
                    int read = socket.read(buffer);

                    buffer.position(0);
                    buffer.limit(read);

                    System.out.println(read);

                    Charset charset = Charset.forName("UTF-8");

                    System.out.println(charset.decode(buffer));

                    buffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
}
