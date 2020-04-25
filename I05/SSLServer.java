import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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

public class SSLServer {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java SSLServer <port> <cypher-suite>*");
            return;
        }

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore", "server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        int port = Integer.parseInt(args[0]);
        String[] cypher_suite = Arrays.copyOf(args, args.length-1);

        SSLServerSocket s;
        SSLServerSocketFactory ssf;

        ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            s = (SSLServerSocket) ssf.createServerSocket(port);
        }
        catch( IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
            return;
        }

        if(cypher_suite.length==0)
            s.setEnabledCipherSuites(ssf.getDefaultCipherSuites());
        else
            s.setEnabledCipherSuites(cypher_suite);

        // lista de todas as entradas já gravadas
        ArrayList<Pair> entries = new ArrayList<>();

        while (true) {
            //Receving Request from Client
            SSLSocket client = (SSLSocket) s.accept();

            // get response
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String received = in.readLine();

            if(received != null){

                System.out.println("SSLServer: " + received);

                String[] parts = received.split(" ");

                String response = " ";

                // REGISTER
                if (parts.length == 3) {
                    for (Pair entry : entries) {

                        if (entry.getDns().equals(parts[1])) {
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

                    for (Pair entry : entries) {

                        if (entry.getDns().equals(parts[1].trim())) {
                            response = entry.getIp();
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
