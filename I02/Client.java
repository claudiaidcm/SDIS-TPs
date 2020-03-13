import java.io.IOException;
import java.net.*;

public class Client extends Thread {
    protected static MulticastSocket socket = null;
    protected static byte[] buf = new byte[256];

    private static String mcast_addr; // IP address of the multicast group used by the server to advertise its service
    private static int mcast_port; // port number of the multicast group used by the server to advertise its
                                   // service

    private static String oper;
    private static String opnd;

    private static String data;

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd> *");
            return;
        }

        mcast_addr = args[0];
        mcast_port = Integer.parseInt(args[1]);
        oper = args[2];
        opnd = args[3];

        multicast();

        service();
    }

    public static void multicast() {
        try {
            socket = new MulticastSocket(mcast_port); // 4446
            InetAddress group = InetAddress.getByName(mcast_addr); // "230.0.0.0"
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            data = received;

            System.out.println(received);

            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void service() {
        try {
            byte[] sbuf;
            String message;

            // OPERAÇÃO DE REGISTO
            if (oper.equals("register")) {
                String[] parts = opnd.split(",");
                String dns_name = parts[0];
                String ip_address = parts[1];
                message = "REGISTER " + dns_name + " " + ip_address;
                sbuf = message.getBytes();
            }
            // OPERAÇÃO DE LOOKUP
            else if (oper.equals("lookup")) {
                message = "LOOKUP " + opnd;
                sbuf = message.getBytes();
            }
            // NENHUMA OPERAÇÃO
            else {
                System.out.println("Usage: <oper> must be either lookup or register");
                return;
            }

            String[] info = data.split(" ");

            // send request
            DatagramSocket socket = new DatagramSocket();
            int port = Integer.parseInt(info[1]);
            InetAddress address = InetAddress.getByName(info[0]);
            DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
            socket.send(packet);

            // get response
            byte[] rbuf = new byte[256];
            DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(rpacket);
            String response = new String(rbuf, 0, packet.getLength());

            socket.close();

            System.out.println("Client: " + oper + " " + opnd + " : " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}