import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Server extends JFrame implements ActionListener {

  private InetAddress ip;
  private InetAddress ip_stream;

  private Map<InetAddress, List<InetAddress>> bootstrapper;
  private Map<InetAddress, String> routing = new HashMap<>(); // tabela de encaminhamento
  private ReentrantLock lock = new ReentrantLock();
  private Map<InetAddress, InetAddress> clients = new HashMap<>(); // lista de clientes e nodos ao qual estão ligados

  private DatagramSocket overlay;
  private DatagramSocket flood;
  private DatagramSocket connect;
  private DatagramSocket openGates;
  private Thread stream;

  private boolean streaming = false;

  // GUI:
  // ----------------
  JLabel label;
  
  // RTP variables:
  // ----------------
  DatagramPacket senddp; // UDP packet containing the video frames (to send)A
  DatagramSocket RTPsocket; // socket to be used to send and receive UDP packet
  int RTP_dest_port = 25000; // destination port for RTP packets
  InetAddress ClientIPAddr; // Client IP address
  static String VideoFileName; // video file to request to the server

  // Video constants:
  // ------------------
  int imagenb = 0; // image nb of the image currently transmitted
  VideoStream video; // VideoStream object used to access video frames
  static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video
  static int FRAME_PERIOD = 100; // Frame period of the video to stream, in ms - standard is 24frames per second
  static int VIDEO_LENGTH = 500; // length of the video in frames

  Timer sTimer; // timer used to send the images at the video frame rate
  byte[] sBuf; // buffer used to store the images to send to the client

  // --------------------------
  // Constructor 1
  // --------------------------
  public Server(String file, InetAddress ipServer) throws IOException {
    parseFile(file);
    this.ip = ipServer;

    try{
      this.overlay = new DatagramSocket(1234, this.ip);      
      this.flood = new DatagramSocket(5678, this.ip);
      this.connect = new DatagramSocket(4567, this.ip);
      this.openGates = new DatagramSocket(6789, this.ip);
    } catch (Exception e){
      e.printStackTrace();
    }

    new Thread(() -> {
      try {
        overlay(file);
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
        connectClient();
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

    stream = new Thread(() -> {
      try {
        mainServer(this.ip_stream);
      } catch (Exception e) {
          e.printStackTrace();
      }
    });
 
  }

  // --------------------------
  // Constructor 2
  // --------------------------
  public Server(InetAddress ip) throws Exception {

    // init Frame
    super("Server");

    // init para a parte do servidor
    sTimer = new Timer(FRAME_PERIOD, this); // init Timer para servidor
    sTimer.setInitialDelay(0);
    sTimer.setCoalesce(true);
    sBuf = new byte[15000]; // allocate memory for the sending buffer

    try {
      RTPsocket = new DatagramSocket(); // init RTP socket

      // é passado diretamente o ip do cliente quando não é passada uma topologia
      // ClientIPAddr = InetAddress.getByName("127.0.0.1");
      ClientIPAddr = ip;

      System.out.println("Servidor: socket " + ClientIPAddr);
      video = new VideoStream(VideoFileName); // init the VideoStream object:
      System.out.println("Servidor: vai enviar video da file " + VideoFileName);

    } catch (SocketException e) {
      System.out.println("Servidor: erro no socket: " + e.getMessage());
    } catch (InterruptedException e) {
      System.out.println("Servidor: erro no video: ");
    }

    // Handler to close the main window
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        // stop the timer and exit
        sTimer.stop();
        System.exit(0);
      }
    });

    // GUI:
    label = new JLabel("Send frame #        ", JLabel.CENTER);
    getContentPane().add(label, BorderLayout.CENTER);

    sTimer.start();
  }

  // ------------------------------------
  // main
  // ------------------------------------
  public void mainServer(InetAddress ip) throws Exception {

    VideoFileName = "movie.Mjpeg";
    System.out.println("Servidor: VideoFileName = " + VideoFileName);

    File f = new File(VideoFileName);
    if (f.exists()) {
      // Create a Main object
      Server s = new Server(ip);
      // show GUI: (opcional!)
      // s.pack();
      // s.setVisible(true);
    } else
      System.out.println("Ficheiro de video não existe: " + VideoFileName);
  }

  // ------------------------
  // Handler for timer
  // ------------------------
  public void actionPerformed(ActionEvent e) {

    // if the current image nb is less than the length of the video
    if (imagenb < VIDEO_LENGTH) {

      // update current imagenb
      imagenb++;

      try {
        // get next frame to send from the video, as well as its size
        int image_length = video.getnextframe(sBuf);

        // Builds an RTPpacket object containing the frame
        RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, sBuf, image_length);

        // get to total length of the full rtp packet to send
        int packet_length = rtp_packet.getlength();

        // retrieve the packet bitstream and store it in an array of bytes
        byte[] packet_bits = new byte[packet_length];
        rtp_packet.getpacket(packet_bits);

        // send the packet as a DatagramPacket over the UDP socket
        senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
        RTPsocket.send(senddp);

        System.out.println("Send frame #"+imagenb);
        // print the header bitstream
        // rtp_packet.printheader();

        // update GUI
        label.setText("Send frame #" + imagenb);
      } catch (Exception ex) {
        System.out.println("Exception caught: " + ex);
        System.exit(0);
      }
    } else {
      // if we have reached the end of the video file, stop the timer
      // sTimer.stop();

      // Reiniciar transmissão
      imagenb = 0; // image nb of the image currently transmitted
      MJPEG_TYPE = 26; // RTP payload type for MJPEG video
      FRAME_PERIOD = 100; // Frame period of the video to stream, in ms
      VIDEO_LENGTH = 500; // length of the video in frames

      System.out.println("Restarting stream and timer");
      sTimer.setInitialDelay(0);
      sTimer.setCoalesce(true);
      sBuf = new byte[15000]; // allocate memory for the sending buffer

      try {
        video = new VideoStream(VideoFileName); // init the VideoStream object:
        System.out.println("Servidor: vai enviar video " + VideoFileName);
      } catch (Exception ex2) {
        System.out.println("Servidor: erro no video: " + ex2.getMessage());
      }
      sTimer.restart();
    }
  }

  public void parseFile(String path) throws IOException {
    try {
      String data = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
      String[] neighbours = data.split(";*(\n)+");

      bootstrapper = new HashMap<>();

      for (String n : neighbours) {
        String[] aux = n.split(":");
        InetAddress node = InetAddress.getByName(aux[0]);
        String[] ips = aux[1].split(",");
        List<InetAddress> nb = new ArrayList<>();

        for (String s : ips) {
          InetAddress ip = InetAddress.getByName(s);
          nb.add(ip);
        }
        bootstrapper.put(node, nb);
      }
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
    }
  }

  public Map.Entry<Packet,InetAddress> receivePacket(DatagramSocket socket) throws IOException {
    byte[] buffer = new byte[1024];
    DatagramPacket data = new DatagramPacket(buffer, buffer.length);
    socket.receive(data);

    buffer = data.getData();
    Packet packet = new Packet(buffer);

    System.out.println("\nServer ---> Data received from: \n  address: " + data.getAddress());
    System.out.println(packet.toString());

    InetAddress nodo = data.getAddress();
    Map.Entry<Packet,InetAddress> res = new AbstractMap.SimpleEntry<Packet,InetAddress>(packet, nodo);

    return res;

  }

  public void sendPacket(InetAddress ip, int port, DatagramSocket socket, int id, int cost, List<InetAddress> neighbours) throws IOException {
    LocalTime time = LocalTime.now();
    Packet p = new Packet(id, cost, time, neighbours);

    byte[] b = p.serialize();
    
    DatagramPacket data = new DatagramPacket(b, b.length, ip, port);
    socket.send(data);

    System.out.println("\nServer ---> Data sent to: \n   address: " + ip);
    System.out.println(p.toString());
  }

  public void overlay(String file) throws IOException {
    // vizinhos do server
    List<InetAddress> serverNb = bootstrapper.get(this.ip); // Primeiro nível da árvore
    for (InetAddress nb : serverNb) {
      routing.put(nb, "off");
      System.out.println(nb.toString() + " gate off");
    }

    while (true) {
      Map.Entry<Packet,InetAddress> packetReceived = receivePacket(overlay);
      Packet packet = packetReceived.getKey();
      InetAddress ip_nodo = packetReceived.getValue() ;

      if (packet.getId() == 0) { // Pedido de overlay
        System.out.println("Starting Overlay at node: " + ip_nodo);

        
        List<InetAddress> neighbours = bootstrapper.get(ip_nodo);
        System.out.println("  Neighbours: " + neighbours + "\n");
        sendPacket(ip_nodo, 1234, overlay, 1, 0, neighbours);
      } else
        routing.put(ip_nodo, "off");
        System.out.println(routing);
    }
  }

  public void flood() throws IOException, InterruptedException {
    List<InetAddress> serverNb = bootstrapper.get(this.ip);
    while (true) {
      for (InetAddress nb : serverNb) {
        sendPacket(nb, 5678, flood, 2, 1, null);
      }
      Thread.sleep(30000); // flood de 30 em 30 s
    }
  } 

  public Boolean compareInetAddress (InetAddress ip_client, InetAddress ip_node) {
    String client = ip_client.getHostAddress();
    String[] c = client.split("\\.");

    String node = ip_node.getHostAddress();
    String[] n = node.split("\\.");

    Boolean aux = true;

    for(int i=0; i<3; i++){
      if(!c[i].equals(n[i])) aux = false;
    }
    if(c[2].compareTo(n[2]) == 1){ // compara o terceiro valor do ip para saber qual o nodo mais próximo do cliente
      aux = true;
    }
    
    return aux;
  }

  public void connectClient() throws IOException {
    while(true){
      Map.Entry<Packet,InetAddress> packetReceived = receivePacket(connect);
      Packet packet = packetReceived.getKey();
      InetAddress ip_client = packetReceived.getValue() ;

      if (packet.getId() == 3) {
        System.out.println("\nClient   " + ip_client + "  connected.\n");
        InetAddress ip_node = null;

        for(InetAddress node : bootstrapper.keySet()){
          if(!node.equals(this.ip)){
            if(compareInetAddress(ip_client, node)){
              ip_node = node;
              this.clients.put(ip_client, ip_node);
            } 
          }
        }

        if(ip_node == null){
	  ip_node = bootstrapper.get(this.ip).get(0);
	  this.clients.put(ip_client, ip_node); // se não estiver na "família" de nenhum nodo liga ao primeiro nodo conectado ao server
	}
        List<InetAddress> nb = new ArrayList<InetAddress>();
        nb.add(ip_node);
        sendPacket(ip_client, 4567, connect, 1, 0, nb);
      }
    }
  }

  public void openGates() throws IOException {
    while(true){
      Map.Entry<Packet,InetAddress> packetReceived = receivePacket(openGates);
      Packet packet = packetReceived.getKey();
      InetAddress ip_nodo = packetReceived.getValue() ;

      if (packet.getId() == 4) { // Abrir comporta de streaming
        try {
          lock.lock();

          if (routing.get(ip_nodo).equals("off")) {
              routing.put(ip_nodo, "on");

              if(streaming == false) {
                  streaming = true;
                  System.out.println("Streaming...");
                  this.ip_stream = ip_nodo;
                  stream.start();
              }
          }
        } finally {
          lock.unlock();
          System.out.println("Gate openned for streaming at node: " + ip_nodo);
        }
      }
      if (packet.getId() == 5) { // Fechar comporta de streaming
        try {
          lock.lock();

          if (routing.get(ip_nodo).equals("on")) {
              routing.put(ip_nodo, "off");

              if(routing.containsValue("on") == false) {
                  streaming = false;
                  System.out.println("Stopping Stream...");
              }
          }
        } finally {
          lock.unlock();
          System.out.println("Gate closed at node: " + ip_nodo);
        }
      }
    }
  }

} // end of Class Servidor
