package de.couchdbexperiment.requests.admin;

import android.content.Context;

import com.android.volley.Request;

import java.util.AbstractMap;
import java.util.concurrent.Future;

import de.couchdbexperiment.Constants;
import de.couchdbexperiment.requests.AbstractRequestCouchbase;
import de.couchdbexperiment.utils.Utils;

/**
 * Login Request.
 * It returns the Session Object: RequestCouchbaseAuthenticationResult.class
 * Created by skip on 04.06.2015.
 */
public class RequestCouchbaseUserCreation extends AbstractRequestCouchbase {

    public static final int METHOD = Request.Method.PUT;
    public static final Constants.Port PORT = Constants.Port.ADMIN;

    public static Future<Object> getAsyncTask(final String username, final String password, final CallBack<Object> callback, Context context){

        // {"name":"yourusername", "password":"yourpassword"}
        AbstractMap.SimpleEntry name = new AbstractMap.SimpleEntry("name", username);
        AbstractMap.SimpleEntry pass = new AbstractMap.SimpleEntry("password", password);
        AbstractMap.SimpleEntry channels = new AbstractMap.SimpleEntry("admin_channels", String.format("[\"%s\"]", Constants.getUserChannel()));
        AbstractMap.SimpleEntry roles = new AbstractMap.SimpleEntry("admin_roles", String.format("[\"%s\"]", Constants.userRole));
        String body = Utils.getRestRequestBody(name, pass, channels, roles);

        String urlUserReg = Constants.urlUserRegistration(PORT, username);
        return getAsyncTask(urlUserReg, METHOD, body, Object.class, callback, context);
    }
}
