package de.couchdbexperiment.requests.admin;

import android.content.Context;

import com.android.volley.Request;

import java.util.AbstractMap;
import java.util.concurrent.Future;

import de.couchdbexperiment.Constants;
import de.couchdbexperiment.requests.AbstractRequestCouchbase;
import de.couchdbexperiment.utils.Utils;
import de.couchdbexperiment.requests.result.RequestCouchbaseAuthenticationResult;

/**
 * Login Request.
 * It returns the Session Object: RequestCouchbaseAuthenticationResult.class
 * Created by skip on 04.06.2015.
 */
public class RequestCouchbaseAuthentication extends AbstractRequestCouchbase {

    public static final int METHOD = Request.Method.POST;
    public static final Constants.Port PORT = Constants.Port.ADMIN;

    public static Future<RequestCouchbaseAuthenticationResult> getAsyncTask(final String username, final String password, final CallBack<RequestCouchbaseAuthenticationResult> callback, final Context context){

        // {"name":"yourusername", "password":"yourpassword"}
        AbstractMap.SimpleEntry name = new AbstractMap.SimpleEntry("name", username);
        AbstractMap.SimpleEntry pass = new AbstractMap.SimpleEntry("password", password);
        String body = Utils.getRestRequestBody(name, pass);

        return getAsyncTask(Constants.urlAuthetication(PORT), METHOD, body, RequestCouchbaseAuthenticationResult.class, callback, context);
    }
}
