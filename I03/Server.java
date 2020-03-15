package example.hello;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.io.*;

public class Server implements ServerInterface {
    private ArrayList<Pair> entries = new ArrayList<Pair>(); // lista de todas as entradas já gravadas

    public Server() {
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <remote_object_name>");
            return;
        }

        Server obj = new Server();

        try {
            obj.setup(args);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void setup(String args[]) throws RemoteException {
        ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);

        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(args[0], stub);
    }

    public String service(String received) {

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

        return response;

    }
}

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