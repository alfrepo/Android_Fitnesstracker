package de.couchdbexperiment.requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.couchdbexperiment.Constants;

/**
 * Created by skip on 04.06.2015.
 */
public abstract class AbstractRequestCouchbase {

    public static final String TAG = "LoginCouchDbActivity";

    final static ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Retrieves a new Aync Task, which will retrieve some data from Server in form of JSon and parse it to an object
     * @param url - request URL
     * @param method - method name
     * @param body - request body
     * @param resultType - to which object should the JSon be converted by gson?
     * @param callback - what to do when ready. May be null.
     * @param context - ApplicaitonContext to use with Volley
     * @param <T> - the type of object, which will be returned by the future
     * @return
     */
    protected static <T> Future<T> getAsyncTask(final String url, final int method, final String body, final Class<T> resultType, final CallBack<T> callback, final Context context) {

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(context);

        final RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(method, url, body, future, future);

        // Add the request to the RequestQueue.
        queue.add(request);

        // switch interface to the Future<T> using gson to parse JSon
        final Future<T> result = new Future<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                JSONObject jj = future.get();
                return parse(jj);
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                JSONObject jj = future.get(timeout, unit);
                return parse(jj);
            }

            private T parse(JSONObject jj){
                String jsonString = jj.toString();
                T parsedObject = Constants.gson.fromJson(jsonString, resultType);
                return parsedObject;
            }
        };


        // attach the callback
        if(callback != null){
            queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request<Object> request) {
                    callback.run(result);
                }
            });
        }


        return result;
    }

    public static abstract class CallBack<T>{
        public abstract void run(Future<T> f);
    }
}
