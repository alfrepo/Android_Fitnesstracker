package de.couchdbexperiment.db;

import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;

import junit.framework.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import de.couchdbexperiment.Constants;
import de.couchdbexperiment.Context;

/**
 * Created by jamesnocentini on 01/02/15.
 */
public class Synchronize {

    public Replication pullReplication;
    public Replication pushReplication;

    private boolean basicAuth;
    private boolean cookieAuth;

    public static class Builder {
        public Replication pullReplication;
        public Replication pushReplication;

        private boolean basicAuth;
        private boolean cookieAuth;

        public Builder(Database database, String url) {

            if (pullReplication == null && pushReplication == null) {

                URL syncUrl;
                try {
                    syncUrl = new URL(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                pullReplication = database.createPullReplication(syncUrl);

                // sync using channels
                if(Constants.isUsingChannels() && Constants.getUserChannel()!=null){
                    // only sync the following channels
                    pullReplication.setChannels(Arrays.asList(Constants.getUserChannel()));
                }

                // sync continous in background. Does not work when syncing on * channel
                pullReplication.setContinuous(true);

                pushReplication = database.createPushReplication(syncUrl);
                pushReplication.setContinuous(true);
            }
        }

        public Builder basicAuth(String username, String password) {

            Authenticator basicAuthenticator = AuthenticatorFactory.createBasicAuthenticator(username, password);
            pullReplication.setAuthenticator(basicAuthenticator);
            pushReplication.setAuthenticator(basicAuthenticator);

            return this;
        }

        public Builder cookieAuth(String cookieValue) {

            String cookieName = "SyncGatewaySession";
            boolean isSecure = false;
            boolean httpOnly = false;

            // expiration date - 1 day from now
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int numDaysToAdd = 1;
            cal.add(Calendar.DATE, numDaysToAdd);
            Date expirationDate = cal.getTime();

            // on default all accessable documents are pull-synced
            pullReplication.setCookie(cookieName, cookieValue, "/", expirationDate, isSecure, httpOnly);
            pushReplication.setCookie(cookieName, cookieValue, "/", expirationDate, isSecure, httpOnly);

            return this;
        }

        public Builder addChangeListener(Replication.ChangeListener changeListener) {
            pullReplication.addChangeListener(changeListener);
            pushReplication.addChangeListener(changeListener);

            return this;
        }

        public Synchronize build() {
            return new Synchronize(this);
        }

    }

    private Synchronize(Builder builder) {
        pullReplication = builder.pullReplication;
        pushReplication = builder.pushReplication;

        basicAuth = builder.basicAuth;
        cookieAuth = builder.cookieAuth;
    }

    public void start() {
        pushReplication.start();
        pullReplication.start();
    }


    private void destroyReplications() {
        try{
            pushReplication.stop();
//            pushReplication.deleteCookie("SyncGatewaySession");
        }catch (IllegalStateException e){
            Log.e(Constants.TAG, "Exception", e);
        }finally {
            pushReplication = null;
        }

        try{
            pullReplication.stop();
//            pullReplication.deleteCookie("SyncGatewaySession");
        }catch (IllegalStateException e){
            Log.e(Constants.TAG, "Exception", e);
        }finally {
            pullReplication = null;
        }
    }


    public static void addSynchronizeToContext(Replication.ChangeListener changeListener){
        // exists already?
        if(Context.synchronize != null){
            return;
        }

        Assert.assertNotNull(Context.database);

        Synchronize sync = new Synchronize.Builder(Context.database, Constants.urlSyncHttp(Constants.Port.NORMAL))
                .basicAuth(Constants.USERNAME, Constants.PASS)
                .addChangeListener(changeListener)
                .build();

        Context.synchronize = sync;
    }

    public static void deleteFromContextAndStop(){
        if(Context.synchronize !=null){
            Context.synchronize.destroyReplications();
            Context.synchronize = null;
        }
    }

}
