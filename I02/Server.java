import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        /*if (args.length != 3) {
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port> ");
            return;
        }*/

        //Multicast multicast = new Multicast(args);

        System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port> ");
    }
}

/*class Multicast implements Runnable{
    private static MulticastSocket socket;
    private static InetAddress group;
    private static byte[] buf;
    private static DatagramPacket packet;

    private static String srvc_port; //port number where the server provides the service
    private static String mcast_addr; //IP address of the multicast group used by the server to advertise its service
    private static int mcast_port; //multicast group port number used by the server to advertise its service

    public Multicast(String[] args) throws IOException {
        this.srvc_port = args[0];
        this.mcast_addr = args[1];
        this.mcast_port = Integer.parseInt(args[2]);

        this.socket = new MulticastSocket();

        this.socket.setTimeToLive(1);
    }

    @Override
    public void run() {
        try {
        this.group = InetAddress.getByName(this.mcast_addr); //"230.0.0.0"

        String message = "localhost " + srvc_port;
        this.buf = message.getBytes();

        this.packet = new DatagramPacket(this.buf, buf.length, this.group, this.mcast_port); //4446
        this.socket.send(packet);

        this.socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/