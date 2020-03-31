import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;

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
        ServerSocket server_socket = new ServerSocket(port_number);

        // lista de todas as entradas já gravadas
        ArrayList<Pair> entries = new ArrayList<Pair>();

        while (true) {
            // get response
            Socket client = server_socket.accept();
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String received = in.readLine();

            if(received != null){


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
            out.println(response);
        }

        }
    }
}
