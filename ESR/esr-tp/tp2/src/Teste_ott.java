import java.io.IOException;
import java.net.InetAddress;

public class Teste_ott {
 

    public static void main(String args[]) throws IOException {

        if (args[0].compareTo("Server") == 0) { // Flag que indica Servidor
            try {
                InetAddress ipServer = InetAddress.getByName(args[2]);
                new Server(args[1], ipServer);  // java Teste_ott Server "bootstrapper.txt" 10.0.2.10
                // java Teste_ott Server "bootstrapper4.txt" 127.0.2.10
            } catch (Exception e){
                System.out.println("Error creating server");
            } 

        } else if (args[0].compareTo("Client") == 0) { // java Teste_ott Client 127.0.0.4 127.0.2.10
            try{
                new Client(InetAddress.getByName(args[1]), InetAddress.getByName(args[2])); // ip do nodo a que se liga
            } catch (Exception e) {
                System.out.println("Error creating client");
            }

        } else { // quando não é passado um tipo de nodo (server ou cliente) abre um nodo de overlay
            try {
                new Node(InetAddress.getByName(args[0]), InetAddress.getByName(args[1])); //java Teste_ott 127.0.0.20 127.0.2.10
            } catch (Exception e) {
                System.out.println("Error creating node");
            }
        } 
    }   
} 