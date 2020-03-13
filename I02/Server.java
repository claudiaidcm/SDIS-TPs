import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port> ");
            return;
        }

        Multicast multicast = new Multicast(args);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(multicast, 1, 1, TimeUnit.SECONDS);

        service(args[0]);
    }

    public static void service(String port_number) {

        try {
            // send request
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(port_number));
            InetAddress address = InetAddress.getByName(port_number);

            // lista de todas as entradas já gravadas
            ArrayList<Pair> entries = new ArrayList<Pair>();

            while (true) {
                // get response
                byte[] rbuf = new byte[256];
                DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
                socket.receive(packet);
                String received = new String(packet.getData());

                System.out.println("Server: " + received);

                String[] parts = received.split(" ");

                String response = " ";

                // REGISTER
                if (parts.length == 3) {
                    for (int i = 0; i < entries.size(); i++) {
                        Pair obj = (Pair) entries.get(i);

                        if (obj.getDns().equals(parts[1])) {
                            response = "-1";
                            break;
                        }
                    }

                    if (!(response.equals("-1"))) {
                        Pair pair = new Pair(parts[1], parts[2]);
                        entries.add(pair);
                        response = String.valueOf(entries.size());
                    }
                }

                // LOOKUP
                else if (parts.length == 2) {
                    response = "NOT_FOUND";

                    for (int i = 0; i < entries.size(); i++) {
                        Pair obj = (Pair) entries.get(i);

                        if (obj.getDns().equals(parts[1].trim())) {
                            response = obj.getIp();
                            break;
                        }
                    }
                }

                // send response
                InetAddress radress = packet.getAddress();
                int rport = packet.getPort();
                byte[] rdata = response.getBytes();
                DatagramPacket rpacket = new DatagramPacket(rdata, rdata.length, radress, rport);
                socket.send(rpacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Multicast implements Runnable {
    private static MulticastSocket socket;
    private static InetAddress group;
    private static byte[] buf;
    private static DatagramPacket packet;

    private static String srvc_port; // port number where the server provides the service
    private static String mcast_addr; // IP address of the multicast group used by the server to advertise its service
    private static int mcast_port; // multicast group port number used by the server to advertise its service

    public Multicast(String[] args) throws IOException {
        srvc_port = args[0];
        mcast_addr = args[1];
        mcast_port = Integer.parseInt(args[2]);

        socket = new MulticastSocket();

        socket.setTimeToLive(1);
    }

    @Override
    public void run() {
        try {
            group = InetAddress.getByName(mcast_addr); // "230.0.0.0"

            String message = "localhost " + srvc_port;
            buf = message.getBytes();

            packet = new DatagramPacket(buf, buf.length, group, mcast_port); // 4446
            socket.send(packet);

            // this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Pair {
    private String dns_name;
    private String ip_address;

    public Pair(String dns_name, String ip_address) {
        this.dns_name = dns_name;
        this.ip_address = ip_address;
    }

    public String getIp() {
        return ip_address;
    }

    public String getDns() {
        return dns_name;
    }
}