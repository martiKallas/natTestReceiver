import com.google.gson.annotations.SerializedName;

public class STUNRegistration {
    @SerializedName("API token")
    public String API_TOKEN = "";
    public String username = "";
    public String token = "";

    public STUNRegistration(UserData user, String api_token){
        API_TOKEN = api_token;
        username = user.username;
        token = user.token;
    }
}
