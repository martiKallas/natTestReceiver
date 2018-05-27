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
    private int nextPort = 15142;
    private Socket nextSocket = null;


    private ConnectionManager(UserData usr){
        user = usr;
    }


    //Returns 0 on sucessful ping of stun server, -1 on timeout
    public synchronized int pingStunServer(int port) throws IOException {
        Socket sock = new Socket();
        sock.setReuseAddress(true);
        sock.bind(new InetSocketAddress(port));
        sock.connect(new InetSocketAddress(STUN_ADDRESS, STUN_PORT), STUN_TIMEOUT);
        STUNRegistration validation = new STUNRegistration(user, API_TOKEN);
        String json = gson.toJson(validation);
        System.out.println(json);
        sendMessage(json, sock);
        String res = "";
        BufferedReader input = getBuffer(sock);
        try {
            res = input.readLine();
        }
        catch(SocketTimeoutException e){
            e.printStackTrace();
            System.out.println("Socket receive timeout");
            sock.close();
            return -1;
        }
        sock.close();
        System.out.println("Response:" + res);
        System.out.println("Port: " + port);
        return 0;
    }

    public synchronized int getNextSocket(){
        try {
            findAvailableSocket();
        }
        catch(SocketException e){
            e.printStackTrace();
            return -1;
        }
        return nextPort;
    }

    //
    private void findAvailableSocket() throws SocketException {
        if (nextPort > 65535) throw new SocketException();
        int check = 0;
        while (true) {
            try {
                check = pingStunServer(nextPort);
                if (check == -1) break;
                else if (check == 0) break;
            }
            catch(IOException e){
                nextPort++;
            }
        }
    }

    private String getMessage() throws IOException, SocketTimeoutException {
        BufferedReader bufferedReader = getBuffer(nextSocket);
        try {
            return bufferedReader.readLine();
        }
        catch(SocketTimeoutException e){
            throw e;
        }
    }


    private BufferedReader getBuffer(Socket connectionClient) throws IOException{
        InputStream inputStream = connectionClient.getInputStream();
        return new BufferedReader((new InputStreamReader(inputStream)));
    }


    private void sendMessage(String message, Socket socket) throws IOException {
        PrintWriter out = writeToBuffer(socket);
        out.println(message);
    }

    private PrintWriter writeToBuffer(Socket socket) throws IOException{
        OutputStream out = socket.getOutputStream();
        return new PrintWriter(out, true);
    }
}
