import java.io.IOException;
import java.net.*;
import java.io.*;

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

        
        int port = Integer.parseInt(args[1]);
        InetAddress address = InetAddress.getByName(args[0]);

        Socket socket = new Socket(address, port);

        // send request
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println(message);


        //get response
       BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       String response = in.readLine();
       if(response != null) {

        System.out.println("Client: " + args[2] + " " + args[3] + " : " + response);
       }

    }
}

