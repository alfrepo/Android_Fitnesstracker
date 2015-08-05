package de.couchdbexperiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by skip on 20.05.2015.
 */
public class Constants {

    public static final String TAG = "de.couchdbexperiment";


    public enum Port {ADMIN, NORMAL};

    // Sets the maximum time to wait for an input stream read to complete before giving up.
    public static final int readTimeout = 10000;

    // Sets the maximum time in milliseconds to wait while connecting to a server.
    public static final int connectTimeout=15000;

    // which encodingto use when talking to the server
    public static final String ENCODING_SERVERREQUEST = "UTF-8";

    // gson
    public static final Gson gson = new GsonBuilder().
            setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").
            create();


    // USER Data
    public static final String USERNAME = "skipxxx";
    public static final String PASS = "pass";

    // SERVER
    public static final String SERVER_ADDRESS = "192.168.191.210";
    public static final int PORT_SYNC_GATEWAY_NORM = 4984;      // 4984 - is the normal port
    public static final int PORT_SYNC_GATEWAY_ADMIN = 4985;     // 4985 - is the admin port.
    public static final String DATABASE_NAME = "gw";

    private static final String urlBody = SERVER_ADDRESS+":%s/"+DATABASE_NAME;
    private static final String urlSyncHttp = "http://"+urlBody;
    private static final String urlSyncHttps = "https://"+urlBody;
    private static final String urlAuthetication = urlSyncHttp+"/_session";
    private static final String urlUsrRegistration = urlSyncHttp+"/_user/%s";

    private static String userChannel = "channel_"+USERNAME;
    public static String userRole = "editor";
    private static boolean isUsingChannels = false;


    public static final String urlSyncHttp(Port p){
        return String.format(urlSyncHttp, get(p));
    }

    public static final String urlSyncHttps(Port p){
        return String.format(urlSyncHttps, get(p));
    }

    public static final String urlAuthetication(Port p){
        return String.format(urlAuthetication, get(p));
    }

    public static final String urlUserInfo(Port p, String userName){
        // same url as for registration
        return urlUserRegistration(p, userName);
    }

    public static final String urlUserRegistration(Port p, String userName){
//        String url = String.format(urlBody, get(p));
//        return String.format("http://%s/_user/%s", url, userName);

          return String.format(urlUsrRegistration, get(p), userName);
    }


    private static final int get(Port p){
        switch (p){
            case ADMIN:
                return PORT_SYNC_GATEWAY_ADMIN;
            case NORMAL:
                return PORT_SYNC_GATEWAY_NORM;
        }
        throw new IllegalStateException();
    }


    public static final void setUserChannel(String channel){
        userChannel = channel;
    }

    public static final String getUserChannel(){
        return userChannel;
    }


    public static boolean isUsingChannels() {
        return isUsingChannels;
    }

    public static void setShouldUseChannels(boolean shouldUseChannels) {
        isUsingChannels = shouldUseChannels;
    }
}
