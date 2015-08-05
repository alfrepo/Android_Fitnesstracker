package de.couchdbexperiment.requests.admin;

import android.content.Context;

import com.android.volley.Request;

import java.util.concurrent.Future;

import de.couchdbexperiment.Constants;
import de.couchdbexperiment.requests.AbstractRequestCouchbase;
import de.couchdbexperiment.requests.result.RequestCouchbaseUserInfoResult;

/**
 * Get userinfo.
 * It returns the Userdata Object: RequestCouchbaseAuthenticationResult.class
 * Created by skip on 04.06.2015.
 */
public class RequestCouchbaseUserInfo extends AbstractRequestCouchbase {

    public static final int METHOD = Request.Method.GET;
    public static final Constants.Port PORT = Constants.Port.ADMIN;

    public static Future<RequestCouchbaseUserInfoResult> getAsyncTask(final CallBack<RequestCouchbaseUserInfoResult> callback, final Context context){

        return getAsyncTask(Constants.urlUserInfo(PORT, Constants.USERNAME), METHOD, null, RequestCouchbaseUserInfoResult.class, callback, context);
    }
}
