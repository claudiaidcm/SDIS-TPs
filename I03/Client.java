package example.hello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {
    }

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Usage: java Server <host_name> <remote_object_name> <oper> <opnd> *");
            return;
        }

        String oper = args[2];
        String opnd = args[3];

        try {
            Registry registry = LocateRegistry.getRegistry(args[0]);
            ServerInterface stub = (ServerInterface) registry.lookup(args[1]);

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

            String response = stub.service(message);
            System.out.println(oper + " " + opnd + " :: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}