package de.couchdbexperiment.requests.result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by skip on 17.05.2015.
 */
public class RequestCouchbaseAuthenticationResult {

    //  "2015-05-20T13:32:25.9999478-07:00"
    public static final String regex = "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})\\.[0-9]*(-[0-9]{2}:[0-9]{2})";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public String cookie_name;
    public String expires;
    public String session_id;

    public String getCookie_name() {
        return cookie_name;
    }

    public void setCookie_name(String cookie_name) {
        this.cookie_name = cookie_name;
    }

    public Date getExpiresDate() {
        Matcher m = Pattern.compile(regex).matcher(expires);
        if(m.matches()){
            String first = m.group(1);
            String second = m.group(2);
            try {
                return format.parse(first+second);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getExpires(){
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s",cookie_name, session_id, expires);
    }
}
