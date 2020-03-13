import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

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

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            return;
        }

        // send request
        int port_number = Integer.parseInt(args[0]);
        DatagramSocket socket = new DatagramSocket(port_number);
        InetAddress address = InetAddress.getByName(args[0]);


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


            //send response
            InetAddress radress = packet.getAddress();
            int rport = packet.getPort();
            byte[] rdata = response.getBytes();
            DatagramPacket rpacket = new DatagramPacket(rdata, rdata.length, radress, rport);
            socket.send(rpacket);

        }
    }
}
