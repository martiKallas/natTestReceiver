import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    public String username;
    public String targetUser;
    public String targetIP = null;
    public String targetPort = null;
    public String requestingUser;
    public String requestingIPaddress = null;
    public String requestingPort = null;
    @SerializedName("API token")
    public String API_token;
    public String usertoken;
    public String date;

    public ChatRequest(UserData user, FriendData friend){
        username = user.username;
        targetUser = friend.friend_name;
        usertoken = user.token;
    }

    public ChatRequest(UserData user, String friendName){
        username = user.username;
        targetUser = friendName;
        usertoken = user.token;
    }
}
