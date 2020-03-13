import java.io.IOException;
import java.net.*;

public class Client extends Thread {
    protected static MulticastSocket socket = null;
    protected static byte[] buf = new byte[256];

    private static String mcast_addr; //IP address of the multicast group used by the server to advertise its service
    private static int mcast_port; //port number of the multicast group used by the server to advertise its service

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd> *");
            return;
        }

        mcast_addr = args[0];
        mcast_port = Integer.parseInt(args[1]);

        run1();
    }

    public static void run1() {
        try {
            socket = new MulticastSocket(mcast_port); //4446
            InetAddress group = InetAddress.getByName(mcast_addr); //"230.0.0.0"
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());

            System.out.println (received);

            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}