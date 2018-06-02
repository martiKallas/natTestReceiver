import com.google.gson.annotations.SerializedName;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class STUNRegistration {
    @SerializedName("API token")
    public String API_TOKEN;
    public String username;
    public String token;
    public String localIPaddress;
    public int port;


    public STUNRegistration(UserData user, String api_token, int port){
        API_TOKEN = api_token;
        username = user.username;
        token = user.token;
        this.port = port;
        try {
            localIPaddress = findLocalIPAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            localIPaddress = null;
            System.out.println("Could not determine local address");
        }
    }

    private String findLocalIPAddress() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        return localhost.getHostAddress().trim();
    }
}
