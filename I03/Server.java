import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements ServerInterface {

    public Server() {
        
    }

    public String service() {
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

                System.out.println(received + " :: " + response);

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

    public static void main(String args[]) {

        try {
            if (args.length != 1) {
                System.out.println("Usage: java Server <remote_object_name>");
            }

            Server obj = new Server();
            Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}