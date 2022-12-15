import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;
import java.time.temporal.ChronoUnit;
import java.awt.event.*;

public class Node {
    private InetAddress ip; // cada nodo vai ter um ip
    private InetAddress previous = null; // permite saber se é a primeira vez ou não que passa no nodo

    private Map<InetAddress, String> routing = new HashMap<>(); // tabela de encaminhamneto de cada nodo (id nodo =>Estado)
    private Map<InetAddress, Integer> cost = new HashMap<>(); // número de saltos desde um nodo até ao atual
    private Map<InetAddress, Long> link_delay = new HashMap<>(); // delay e address do vizinho
    private ReentrantLock lock = new ReentrantLock();

    private DatagramSocket overlay;
    private DatagramSocket flood;
    private DatagramSocket active;
    private DatagramSocket openGates;

    private Map<InetAddress, SimpleEntry<Integer,Integer>> clientsTimer = new HashMap<>();

    // RTP variables:
    // ----------------
    DatagramPacket rcvdp; // UDP packet received from the server (to receive)
    DatagramSocket RTPsocket; // socket to be used to send and receive UDP packet
    static int RTP_RCV_PORT = 25000; // port where the client will receive the RTP packets

    Timer cTimer; // timer used to receive data from the UDP socket
    byte[] cBuf; // buffer used to store data received from the server

    public Node(InetAddress ipNode, InetAddress ipServer) throws IOException {
        this.ip = ipNode;

        try{
            this.overlay = new DatagramSocket(1234, this.ip);
            this.flood = new DatagramSocket(5678, this.ip);
            this.active = new DatagramSocket(2345, this.ip);
            this.openGates = new DatagramSocket(6789, this.ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        new Thread(() -> {
          try {
            overlay(ipServer);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        }).start();
        

        new Thread(() -> {
            try {
              flood();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }).start();

        new Thread(() -> {
            try {
                clientActive();
            } catch (Exception e) {
              e.printStackTrace();
            }
        }).start();  
  
        new Thread(() -> {
            try {
              openGates();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }).start();

	new Thread(this::rtpTimer).start();
    }


    public void rtpTimer() {

        // init para a parte do nodo
        // --------------------------
	System.out.println("Streaming activated\n");
        cTimer = new Timer(20, new Node.nodeTimerListener());
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);
        cBuf = new byte[15000]; // allocate enough memory for the buffer used to receive data from the server
        cTimer.start();

        try {
            // socket e video
            RTPsocket = new DatagramSocket(RTP_RCV_PORT); // init RTP socket
            RTPsocket.setSoTimeout(5000); // set timeout to 5s
        } catch (SocketException e) {
            System.out.println("Nodo: erro no socket: " + e.getMessage());
        }
    }

    // ------------------------------------
    // Handler for timer (para nodo)
    // ------------------------------------

    class nodeTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(cBuf, cBuf.length);

            try {
                // receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                // create an RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                for (InetAddress ip : routing.keySet()) {
                    if (!ip.equals(previous)) {
                        if (routing.get(ip).equals("on")) {
                            // get the payload bitstream from the RTPpacket object
                            int length = rtp_packet.getlength();
                            byte[] packet = new byte[length];
                            rtp_packet.getpacket(packet);

                            DatagramPacket dp = new DatagramPacket(packet, length, ip, RTP_RCV_PORT);
                            RTPsocket.send(dp);
                        }
                    }
                }
            } catch (InterruptedIOException iioe) {
                System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("Exception caught: " + ioe);
            }

        }
    }

    public Map.Entry<Packet,InetAddress> receivePacket(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
        socket.receive(data);
    
        buffer = data.getData();
        Packet packet = new Packet(buffer);

        System.out.println("Node ---> Data received from: \n  address: " + data.getAddress() + "\n");
        System.out.println(packet.toString());
    
        InetAddress nodo = data.getAddress();
        Map.Entry<Packet,InetAddress> res = new AbstractMap.SimpleEntry<Packet,InetAddress>(packet, nodo);
    
        return res;
    
      }

    public void sendPacket(InetAddress ip, int port, DatagramSocket socket, int id, int cost, LocalTime time, List<InetAddress> neighbours) throws IOException {
        
        Packet p = new Packet(id, cost, time, neighbours);

        byte[] b = p.serialize();

        DatagramPacket data = new DatagramPacket(b, b.length, ip, port);
        socket.send(data);

        System.out.println("\nNode ---> Data sent to: \n   address: " + ip);
        System.out.println(p.toString());
    }

    public void overlay(InetAddress server) throws IOException {
        LocalTime time = LocalTime.now();
        sendPacket(server, 1234, overlay, 0, 0, time, null); // pedido dos vizinhos

        Map.Entry<Packet,InetAddress> packetReceived = receivePacket(overlay);
        Packet packet = packetReceived.getKey();

        for (InetAddress nb : packet.getNeighbours()) {
            routing.put(nb, "off");
        }
    }


    public void flood() throws IOException {
        while(true){
            
            Map.Entry<Packet,InetAddress> packetReceived = receivePacket(flood);
            Packet packet = packetReceived.getKey();
            InetAddress ip_origin = packetReceived.getValue() ;

            if(packet.getId() == 2){
                int nodeCost = packet.getCost();

                LocalTime currentTime = LocalTime.now();
                long delay = packet.getDelay().until(currentTime, ChronoUnit.MILLIS);

                if(this.previous == null){
                    previous = ip_origin;

                    cost.put(ip_origin, nodeCost);
                    link_delay.put(ip_origin, delay);

                    for(InetAddress nb : routing.keySet()){
                        if(!nb.equals(previous)) sendPacket(nb, 5678, flood, 2, nodeCost+1, packet.getDelay(), null);
                    }
                } else {
                    if (delay < link_delay.get(previous) || (delay == link_delay.get(previous) && nodeCost < cost.get(previous))){
                        previous = ip_origin;

                        cost.put(ip_origin, nodeCost);
                        link_delay.put(ip_origin, delay);

                        
                    } 
                    for(InetAddress nb : routing.keySet()){
                        if(!nb.equals(previous)) sendPacket(nb, 5678, flood, 2, nodeCost+1, packet.getDelay(), null);
                    }
                }  
            } 
            try{
                Thread.sleep(60);
            } catch (InterruptedException e){
                System.out.println("Flooding interrupted");
            }  
        }
    }

    public void clientActive() throws IOException, InterruptedException {
        while(true){
            for (InetAddress client : clientsTimer.keySet()){
                
                sendPacket(client, 2345, active, 6, 0, LocalTime.now() , null);

		try{
                    lock.lock();
		    int countP4 = clientsTimer.get(client).getKey();
		    int countP6 = clientsTimer.get(client).getValue();
                    SimpleEntry<Integer,Integer> aux = new AbstractMap.SimpleEntry<Integer,Integer>(countP4, countP6+1);
                    clientsTimer.put(client, aux);

		    if (countP6 - countP4 >= 3){
		    	sendPacket(previous, 6789, openGates, 5, 0, LocalTime.now(), null);
			SimpleEntry<Integer,Integer> restartClientTimer = new AbstractMap.SimpleEntry<Integer,Integer>(0, 0);
			clientsTimer.put(client, restartClientTimer);
		    }
                } finally{
                    lock.unlock();
                }
            }
            Thread.sleep(2000);
        }
    }


    

    public void openGates() throws IOException {
        while(true){
          Map.Entry<Packet,InetAddress> packetReceived = receivePacket(openGates);
          Packet packet = packetReceived.getKey();
          InetAddress ip_nodo = packetReceived.getValue() ;
    
            if (packet.getId() == 4) { // Abrir comporta de streaming
                if (previous != null) {
                    routing.put(ip_nodo, "on");
                    System.out.println("Gate openned for streaming at node: " + ip_nodo);

                    if(clientsTimer.containsKey(ip_nodo)) {
                        try{
                            lock.lock();
			    int countP4 = clientsTimer.get(ip_nodo).getKey();
			    int countP6 = clientsTimer.get(ip_nodo).getValue();
                            SimpleEntry<Integer,Integer> aux = new AbstractMap.SimpleEntry<Integer,Integer>(countP4+1, countP6);
                            clientsTimer.put(ip_nodo, aux);
                        } finally{
                            lock.unlock();
                        }
                    } else {
			SimpleEntry<Integer,Integer> initClientTimer = new AbstractMap.SimpleEntry<Integer,Integer>(0, 0);
			clientsTimer.put(ip_nodo, initClientTimer);
		    }

 
                    sendPacket(previous, 6789, openGates, 4, 0, packet.getDelay(), null);
                }
            }



            if (packet.getId() == 5) { // Fechar comporta de streaming
		routing.put(ip_nodo, "off");
            	System.out.println("Gate closed at node: " + ip_nodo);

                if (routing.containsValue("on") == false) {
                    sendPacket(previous, 6789, openGates, 5, 0, packet.getDelay(), null);
                }
                
            }
        }
    }
    
}

