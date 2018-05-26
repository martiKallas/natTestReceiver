import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {
    //@Expose allows us to ignore fields without expose in creating some requests
    @Expose
    public String username = "";
    //TODO: how to get/set password securely
    @Expose
    public String password = "";
    @Expose
    public String email = "";
    @Expose
    public String ipAddress = "127.0.0.1";
    @Expose
    public String peerServerPort = "9001";
    public String token = "";
    @Expose
    @SerializedName("API token")
    public String API_token = "";
    //TODO: change user id
    public long id = 3;
    public UserData(){};
};
