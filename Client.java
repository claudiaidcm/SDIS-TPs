import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length < 4) {
            System.out.println("Usage: java Client <hostname> <port number> <oper> <opnd>*");
            return;
        }

        String oper = args[2];
        String opnd = args[3];

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

        // send request
        DatagramSocket socket = new DatagramSocket();
        int port = Integer.parseInt(args[1]);
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);


        //get response
        byte[] rbuf = new byte[256];
        DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(rpacket);
        String response = new String(rbuf, 0, packet.getLength());

        socket.close();

        System.out.println("Client: " + args[2] + " " + args[3] + " : " + response);

    }
}

