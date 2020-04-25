import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class SSLClient {
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            return;
        }

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStore", "client.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String[] cypher_suite = Arrays.copyOf(args, args.length-4);

        SSLSocket s;
        SSLSocketFactory sf;

        sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            s = (SSLSocket) sf.createSocket(host, port);
        }
        catch( IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
            return;
        }

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        if(cypher_suite.length==0)
            s.setEnabledCipherSuites(ssf.getDefaultCipherSuites());
        else
            s.setEnabledCipherSuites(cypher_suite);

        String oper = args[2];
        String opnd = args[3];

        String message;

        // OPERAÇÃO DE REGISTO
        if (oper.equals("register")) {
            String[] parts = opnd.split(",");
            String dns_name = parts[0];
            String ip_address = parts[1];
            message = "REGISTER " + dns_name + " " + ip_address;
        }
        // OPERAÇÃO DE LOOKUP
        else if (oper.equals("lookup")) {
            message = "LOOKUP " + opnd;
        }
        // NENHUMA OPERAÇÃO
        else {
            System.out.println("Usage: <oper> must be either lookup or register");
            return;
        }

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream()));
        out.println(message);

        String received =  in.readLine();
        System.out.println("SSLClient: " + oper + " " + opnd + " : " + received);
    }
}
