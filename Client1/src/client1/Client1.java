package client1;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class Client1 implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    public String card1[] = {"jc", "9c", "ac", "tc",
        "kc", "qc", "8c", "7c", "jh", "9h", "ah", "th", "kh", "qh", "8h", "7h", "js", "9s", "as", "ts", "ks", "qs", "8s", "7s", "jd", "9d", "ad", "td", "kd", "qd", "8d", "7d"
    };

    static Frame1 f1 = new Frame1();
    static String trump=null;

    public void oos(String s) {
        os.println(s);
    }

    public void slp() {
        try {
            sleep(2500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        f1.setVisible(true);
        int portNumber = 2222;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.parseInt(args[1]);
        }

        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }

        /*
         * If everything has been initialized then we want to write some data to the
         * socket we have opened a connection to on the port portNumber.
         */
        if (clientSocket != null && os != null && is != null) {
            try {

                /* Create a thread to read from the server. */
                new Thread(new Client1()).start();
                while (!closed) {

                    os.println(inputLine.readLine());
                }
                /*
                 * Close the output stream, close the input stream, close the socket.
                 */
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;
        
        try {
            int k = 0;
            while ((responseLine = is.readLine()) != null) {

                boolean string = true;

                for (int i = 0; i < 32; i++) {
                    if (card1[i].equals(responseLine)) {
                        f1.printy(responseLine, k);
                        //System.out.println("k1="+k);

                        k++;
                        if (k > 7) {
                            k = 0;
                        }
                        string = false;
                    }
                }
                if (string) {

                    if (responseLine.startsWith("p2") || responseLine.startsWith("p3") || responseLine.startsWith("p4")) {
                        f1.setp2(responseLine);
                    } else if (responseLine.startsWith("pass")) {
                        f1.paneloff();
                    }
                    else if (responseLine.startsWith("tr")) {
                        //trump=responseLine.substring(2);
                        f1.trumpiconset(responseLine.charAt(2));
                        
                    }
                    else if (responseLine.startsWith("Player 1 took")) {
                        f1.showtrump();
                    }
                    else if (responseLine.startsWith("Lastwinner")) {
                        f1.showwinner(responseLine);
                    }
                    else if (responseLine.startsWith("op")) {
                        f1.showicon();
                    }
                    else if (responseLine.startsWith("on")) {
                        f1.on();
                    }
                    
                    else if (responseLine.startsWith("new")) {
                        f1.panelon();
                    } else {
                        System.out.println(responseLine);
                    }
                    // if(f.p2!=null)os.println(f.p2.trim());
                    //  System.out.println(f.p2);
                }
                //if(f.p2!=null)os.println(f.p2.trim());
            }

            closed = true;

        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
