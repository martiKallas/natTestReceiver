import com.google.gson.Gson;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

//Singleton - tracks open connections and sockets
//  used to provide peer connection with port used to establish IP and port on
//  STUN server and passed to various windows to properly close connections
//  on program exit
public class ConnectionManager {
    private static ConnectionManager manager = null;
    public synchronized static ConnectionManager getConnectionManager(UserData user)throws IOException{
        if (manager == null) manager = new ConnectionManager(user);
        return manager;
    }

    private static final String API_TOKEN =  "fXtas7yB2HcIVoCyyQ78";
    //Remote: "hwsrv-265507.hostwindsdns.com"
    private static final String STUN_ADDRESS = "hwsrv-265507.hostwindsdns.com";
    //private static final String STUN_ADDRESS = "localhost";
    private static final int STUN_PORT = 15000;
    private static final int STUN_TIMEOUT = 10*1000;
    private static Gson gson = new Gson();
    private UserData user = null;
    private ArrayList<Socket> openSockets = new ArrayList<>();
    private int nextPort = 59870;
    private Socket nextSocket = null;


    private ConnectionManager(UserData usr){
        user = usr;
    }

    public synchronized int getNextSocket(){
        try {
            findNextSocket();
        }
        catch(SocketException e){
            e.printStackTrace();
            return -1;
        }
        int temp = nextPort;
        nextPort++; //increment to avoid conflicts
        return temp;
    }

    private void findNextSocket() throws SocketException {
        if (nextPort > 65535){
            nextPort = 15000;
        }
        while (true) {
            try {
                nextSocket = new Socket();
                nextSocket.setReuseAddress(true);
                nextSocket.bind(new InetSocketAddress(nextPort));
                nextSocket.connect(new InetSocketAddress(STUN_ADDRESS, STUN_PORT), STUN_TIMEOUT);
                STUNRegistration validation = new STUNRegistration(user, API_TOKEN, nextPort);
                String json = gson.toJson(validation);
                System.out.println(json);
                sendMessage(json);
                String res = "";
                try {
                    System.out.println("Connected to STUN on port: " + nextPort);
                    res = getMessage();
                }
                catch(SocketTimeoutException e){
                    e.printStackTrace();
                    System.out.println("Socket receive timeout");
                    nextSocket.close();
                    throw new SocketException();
                }
                nextSocket.close();
                System.out.println("Good STUN Response:" + res);
                return;
            }
            catch(IOException e){
                System.out.println("Socket exception, trying next port");
                nextPort++;
            }
        }
    }

    private String getMessage() throws IOException, SocketTimeoutException {
        BufferedReader bufferedReader = getBuffer();
        try {
            return bufferedReader.readLine();
        }
        catch(SocketTimeoutException e){
            throw e;
        }
    }


    private BufferedReader getBuffer() throws IOException{
        InputStream inputStream = nextSocket.getInputStream();
        return new BufferedReader((new InputStreamReader(inputStream)));
    }


    private void sendMessage(String message) throws IOException {
        PrintWriter out = writeToBuffer();
        out.println(message);
    }

    private PrintWriter writeToBuffer() throws IOException{
        OutputStream out = nextSocket.getOutputStream();
        return new PrintWriter(out, true);
    }
}
