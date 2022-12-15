/*------------------
Cliente
usage: java Cliente
adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
colocar o cliente primeiro a correr que o servidor dispara logo!
---------------------- */

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Client {

    private InetAddress ip;
    private InetAddress ipNode;

    private Map<InetAddress, Integer> cost = new HashMap<>(); // número de saltos desde um nodo até ao atual
    private Map<InetAddress, Long> link_delay; // delay e address do vizinho

    private DatagramSocket connect;
    private DatagramSocket openGates;

    // GUI
    // ----
    JFrame f = new JFrame("Cliente de Testes");
    JButton setupButton = new JButton("Setup");
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton tearButton = new JButton("Teardown");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel iconLabel = new JLabel();
    ImageIcon icon;

    // RTP variables:
    // ----------------
    DatagramPacket rcvdp; // UDP packet received from the server (to receive)
    DatagramSocket RTPsocket; // socket to be used to send and receive UDP packet
    static int RTP_RCV_PORT = 25000; // port where the client will receive the RTP packets

    Timer cTimer; // timer used to receive data from the UDP socket
    byte[] cBuf; // buffer used to store data received from the server


    public Client(InetAddress ip, InetAddress ipServer) throws IOException { 
        this.ip = ip;

        try{
            this.connect = new DatagramSocket(4567, this.ip);
            this.openGates = new DatagramSocket(6789, this.ip);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
              connect(ipServer);
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

        new Thread(() -> {
            try {
              client();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
  
    }

    
    // --------------------------
    // Constructor
    // --------------------------
    public void client() {

        // build GUI
        // --------------------------
        // Frame
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Buttons
        buttonPanel.setLayout(new GridLayout(1, 0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);

        // handlers...
        playButton.addActionListener(new playButtonListener());
        //pauseButton.addActionListener(new pauseButtonListener());
        tearButton.addActionListener(new tearButtonListener());

        // Image display label
        iconLabel.setIcon(null);

        // frame layout
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0, 0, 380, 280);
        buttonPanel.setBounds(0, 280, 380, 50);

        f.getContentPane().add(mainPanel, BorderLayout.CENTER);
        f.setSize(new Dimension(390, 370));
        f.setVisible(true);

        // init para a parte do cliente
        // --------------------------
        cTimer = new Timer(20, new clientTimerListener());
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);
        cBuf = new byte[15000]; // allocate enough memory for the buffer used to receive data from the server

        try {
            // socket e video
            RTPsocket = new DatagramSocket(RTP_RCV_PORT); // init RTP socket (o mesmo para o cliente e servidor)
            RTPsocket.setSoTimeout(5000); // set timeout to 5s
        } catch (SocketException e) {
            System.out.println("Cliente: erro no socket: " + e.getMessage());
        }
    }

    // ------------------------------------
    // Handler for buttons
    // ------------------------------------

    // Handler for Play button
    // -----------------------
    class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            System.out.println("Play Button pressed !");
            // start the timer ...
            cTimer.start();
        }
    }

    // Handler for tear button
    // -----------------------
    class tearButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            System.out.println("Teardown Button pressed !");
            // stop the timer
            cTimer.stop();
            // exit
            System.exit(0);
        }
    }

    // Handler for Pause button
    // -----------------------
    class pauseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
    
        System.out.println("Pause Button pressed !"); 
              //stop the timer ... 
              cTimer.stop();
            }
    }
  

    // ------------------------------------
    // Handler for timer (para cliente)
    // ------------------------------------

    class clientTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            // Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(cBuf, cBuf.length);

            try {
                // receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                // create an RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                // print important header fields of the RTP packet received:
                //System.out.println("Got RTP packet with SeqNum # " + rtp_packet.getsequencenumber() + " TimeStamp "
                //        + rtp_packet.gettimestamp() + " ms, of type " + rtp_packet.getpayloadtype());
		System.out.println("Got RTP packet with SeqNum # " + rtp_packet.getsequencenumber());

                // print header bitstream:
                //rtp_packet.printheader();

                // get the payload bitstream from the RTPpacket object
                int payload_length = rtp_packet.getpayload_length();
                byte[] payload = new byte[payload_length];
                rtp_packet.getpayload(payload);

                // get an Image object from the payload bitstream
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.createImage(payload, 0, payload_length);

                // display the image as an ImageIcon object
                icon = new ImageIcon(image);
                iconLabel.setIcon(icon);
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

        System.out.println("\nClient ---> Data received from: \n  address: " + data.getAddress() + "\n");
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

        System.out.println("\nClient ---> Data sent to: \n   address: " + ip);
        System.out.println(p.toString());
    }

    public void connect(InetAddress ipServer) throws IOException {
        sendPacket(ipServer, 4567, connect, 3, 0, null);

        Map.Entry<Packet,InetAddress> packetReceived = receivePacket(connect);
        Packet packet = packetReceived.getKey();
        this.ipNode = packet.getNeighbours().get(0);

	System.out.println("\n\nIP FORNECIDO " + ipNode);
        
    }

    public void openGates() throws IOException, InterruptedException {
        while(true) {
            Thread.sleep(5000);
            sendPacket(this.ipNode, 6789, openGates, 4, 0, null);
        }
    }

}// end of Class Cliente
