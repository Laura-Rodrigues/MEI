import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;


public class Packet implements Serializable{
    private int id_packet;
    private int cost; // Number of jumps
    private LocalTime delay;  // Time of creation
    private List<InetAddress> neighbours;

    public Packet(int id, int cost, LocalTime delay, List<InetAddress> nb){
        this.id_packet = id;
        this.cost = cost;
        this.delay = delay;
        this.neighbours = nb;
    }

    public Packet(byte[] info){
        try{
            Packet res = deserialize(info);
            this.id_packet = res.getId();
            this.cost = res.getCost();
            this.delay = res.getDelay();
            this.neighbours = res.getNeighbours();
        } catch (Exception e) {
            System.out.println("Packet not created.");
        }
    }

    // Get methods
    public int getId(){
        return this.id_packet;
    }

    public int getCost(){
        return this.cost;
    }

    public LocalTime getDelay(){
        return this.delay;
    }
    
    public List<InetAddress> getNeighbours (){ 
        return this.neighbours;
    }

 //Serialize
    public byte[] serialize () throws IOException {
        
        byte[] packet;
        ByteArrayOutputStream file = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(this);
        out.close();
        
        packet = file.toByteArray();
        return packet;

    }
 
 //Deserialize
    public Packet deserialize (byte[] info) throws IOException, ClassNotFoundException {
        
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(info));
        Packet res = (Packet) in.readObject();
        in.close();

        return res;
    }

    public String toString (){
        StringBuilder sb = new StringBuilder("");
        sb.append("Packet: \n   Id: ").append(this.id_packet).append("\n");
        sb.append("   Cost: ").append(this.cost).append("\n");
        sb.append("   Time of departure: ").append(this.delay).append("\n");
        sb.append("   Neighbours: ").append(this.neighbours).append("\n\n");
        return sb.toString();
    }


}



