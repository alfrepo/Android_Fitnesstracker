package de.couchdbexperiment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import org.apache.http.client.HttpResponseException;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.couchdbexperiment.db.Database;
import de.couchdbexperiment.db.Synchronize;
import de.couchdbexperiment.db.documents.Document;
import de.couchdbexperiment.requests.AbstractRequestCouchbase;
import de.couchdbexperiment.requests.admin.RequestCouchbaseAuthentication;
import de.couchdbexperiment.requests.admin.RequestCouchbaseUserCreation;
import de.couchdbexperiment.requests.admin.RequestCouchbaseUserInfo;
import de.couchdbexperiment.requests.result.RequestCouchbaseAuthenticationResult;
import de.couchdbexperiment.requests.result.RequestCouchbaseUserInfoResult;
import de.couchdbexperiment.ui.dialogs.MProgressDialog;


public class MainCouchDbActivity extends ActionBarActivity {

    public static final String TAG = "LoginCouchDbActivity";


    public static final String serverAddress = Constants.SERVER_ADDRESS;
    public static final String databaseName = Constants.DATABASE_NAME;

    public Button purgeButton;
    public Button recreateDbButton;
    public Button syncButtonStop;
    public Button syncButtonStart;
    public Button createDocButton;
    public Button buttonListDataUpdate;
    public Button buttonCreateUser;
    public ListView listView;
    public EditText channelNameTextView;
    public CheckBox checkBoxUseChannel;

    private MProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_couch_db);

        purgeButton = (Button) findViewById(R.id.purgeButton);
        recreateDbButton = (Button) findViewById(R.id.recreateDbButton);

        syncButtonStart = (Button) findViewById(R.id.syncButtonStart);
        syncButtonStop = (Button) findViewById(R.id.syncButtonStop);
        createDocButton = (Button) findViewById(R.id.createDocButton);
        buttonListDataUpdate = (Button) findViewById(R.id.buttonUpdatalistdata);
        listView = (ListView) findViewById(R.id.listView);
        channelNameTextView = (EditText) findViewById(R.id.channelNameTextView);
        checkBoxUseChannel = (CheckBox) findViewById(R.id.checkBoxUseChannel);
        buttonCreateUser = (Button) findViewById(R.id.buttonCreateUser);

        // remember the app context
        Context.androidContext = getApplicationContext();

        // init the database representation
        de.couchdbexperiment.db.Database.addDatabaseToContext(getApplicationContext());

        // init the Synchronized object
        Synchronize.addSynchronizeToContext(getReplicationChangeListener());

        // TODO: visualize successfull / unsucessfull sync start. Use the callback for that
        // login or register if necessary and start syncronization
        AsyncTaskLoginAndSync a = new AsyncTaskLoginAndSync(null);
        a.execute();

        setupButtons();

        // callback to add some data into the db, as soon as login is done
//        TestDocument.purgeAllDocumentsInLocalDb();
//        TestDocument.createInDb(USERNAME);
//        TestDocument.createInDb(USERNAME);
//        TestDocument.createInDb(USERNAME);
//        TestDocument.createInDb(USERNAME);
//        TestDocument.createInDb(USERNAME);


        // show the docs in list
        displayDocumentsInList();
    }

    private void setupButtons() {
        purgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document.purgeAllDocumentsInLocalDb();
                displayDocumentsInList();
            }
        });

        recreateDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.deleteDatabaseFromContext();
                Synchronize.deleteFromContextAndStop();
                Database.addDatabaseToContext(getApplicationContext());
                Synchronize.addSynchronizeToContext(getReplicationChangeListener());

                displayDocumentsInList();
            }
        });

        syncButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Context.synchronize == null){
                    Synchronize.addSynchronizeToContext(getReplicationChangeListener());
                }
                Context.synchronize.start();
            }
        });

        syncButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context.synchronize.deleteFromContextAndStop();
            }
        });

        createDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> properties = new HashMap<String, Object>();
                properties.put("title", "UsersTestDocumenttitle");
                properties.put("checked", Boolean.FALSE);

                Document.createInDb(properties, "UsersTestDocument");
                displayDocumentsInList();
            }
        });


        buttonListDataUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDocumentsInList();
            }
        });

        buttonCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AbstractRequestCouchbase.CallBack<Object> callBack = new AbstractRequestCouchbase.CallBack() {
                    @Override
                    public void run(Future f) {
                        try {
                            Object result = f.get();
                            Log.d(Constants.TAG, "Ready");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RequestCouchbaseUserCreation.getAsyncTask(Constants.USERNAME, Constants.PASS, callBack, getApplicationContext());
            }
        });


        // init
        channelNameTextView.setText(Constants.getUserChannel());
        // listener which will sync from textView to Constants
        channelNameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // before the text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // on text change
            }

            @Override
            public void afterTextChanged(Editable s) {
                // get the new string
                Constants.setUserChannel(channelNameTextView.getText().toString());
            }
        });

        // init
        checkBoxUseChannel.setChecked(Constants.isUsingChannels());
        checkBoxUseChannel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.setShouldUseChannels(isChecked);
            }
        });
    }

    private void displayDocumentsInList() {

        // check database connection
        if(Context.database ==null){
            return;
        }

        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    Query q = Document.getAllDocsQuery();
                    QueryEnumerator enumerator = q.run();

                    QueryAdapter a = new QueryAdapter(MainCouchDbActivity.this, q);
                    listView.setAdapter(a);

                    a.notifyDataSetChanged();

                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Te class:
     * - logs in or registers
     * - starts the syncronization
     */
    class AsyncTaskLoginAndSync extends AsyncTask<Object, Object, Boolean> {

        Runnable mCallback;

        AsyncTaskLoginAndSync(Runnable callback){
            mCallback = callback;
        }

        @Override
        protected void onCancelled() {
            if(mCallback!=null){
                mCallback.run();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(mCallback!=null){
                mCallback.run();
            }
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            // show Dialog
            showProgressDialog();

            try {
                RequestCouchbaseAuthenticationResult r;
                Future<RequestCouchbaseAuthenticationResult> loginTask;
                Future<RequestCouchbaseUserInfoResult> userInfoTask;

                // user info
                userInfoTask = getUserInfo();
                RequestCouchbaseUserInfoResult userinfo = userInfoTask.get(2, TimeUnit.SECONDS);

//                // get session
//                loginTask = getSession();
//                r = loginTask.get(2, TimeUnit.SECONDS);

                // if sessionId does not exist - the user does not exist yet
                if(userinfo == null ){

                    // try to register
                    Future<Object> task = registerNewUser();
                    task.get(2, TimeUnit.SECONDS);; // wait for result

                    // try to get the session now
//                    loginTask = getSession();
//                    r = loginTask.get(2, TimeUnit.SECONDS);

                    userInfoTask = getUserInfo();
                    userinfo = userInfoTask.get(2, TimeUnit.SECONDS);
                }

                // check whether the Registration has worked now
                if(userInfoTask==null ){
                    Log.e(Constants.TAG, "Login failed");
                    toastFromUIThread("LOGIN FAILED!");
                    return false;
                }

                // registration / login worked. Start the sync
                Context.synchronize.addSynchronizeToContext(getReplicationChangeListener());

                // success
                return true;

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally {
                // hide the Dialog
                hideProgressDialog();
            }

            return false;
        }
    }

    private Future<Object> registerNewUser(){
        Future<Object> asyncTask = RequestCouchbaseUserCreation.getAsyncTask(Constants.USERNAME, Constants.PASS, null, MainCouchDbActivity.this);
        return asyncTask;
    }

    private Future<RequestCouchbaseUserInfoResult> getUserInfo() {
        Future<RequestCouchbaseUserInfoResult> authenticationTask = RequestCouchbaseUserInfo.getAsyncTask(null, MainCouchDbActivity.this);
        return authenticationTask;
    }

    private Future<RequestCouchbaseAuthenticationResult> getSession() {
        Future<RequestCouchbaseAuthenticationResult> authenticationTask = RequestCouchbaseAuthentication.getAsyncTask(Constants.USERNAME, Constants.PASS, null, MainCouchDbActivity.this);
        return authenticationTask;
    }





    // TODO implement
    private Replication.ChangeListener getReplicationChangeListener() {
        return new Replication.ChangeListener() {

            @Override
            public void changed(Replication.ChangeEvent event) {
                // what to do on replicaton changes
                Log.d(Constants.TAG, "sync update happened: " + event);

                Replication replication = event.getSource();

                if (event.getError() != null) {
                    showSyncError(event.getError());
                }
                Log.d(TAG, event.toString());
                updateSyncProgress(
                        replication.getCompletedChangesCount(),
                        replication.getChangesCount(),
                        replication.getStatus(),
                        replication.isPull()? "PULL" : "PUSH"
                );

                displayDocumentsInList();
            }
        };
    }

    private void updateSyncProgress(final int completedChanges, final int changeCount,final  Replication.ReplicationStatus status, final String direction) {
        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = String.format("%s : Completed changes: %s, change count %s, ReplicationStatus: %s", direction, completedChanges, changeCount, status.name());
                Log.d(Constants.TAG, message);
                Toast.makeText(MainCouchDbActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSyncError(final Throwable lastError) {
        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (lastError.getMessage().contains("existing change tracker")) {
                    Toast.makeText(MainCouchDbActivity.this, String.format("Sync error: %s:", lastError.getMessage()), Toast.LENGTH_LONG).show();
                }
                if (lastError instanceof HttpResponseException) {
                    HttpResponseException responseException = (HttpResponseException) lastError;
                    if (responseException.getStatusCode() == 401) {
                        Toast.makeText(MainCouchDbActivity.this, String.format("Unauthorized error: %s:", lastError.getMessage()), Toast.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(MainCouchDbActivity.this, String.format("Sync failed: %s:", lastError.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showProgressDialog(){
        hideProgressDialog();
        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog = new MProgressDialog(MainCouchDbActivity.this, "Logging in into the SYSTEM...");
                mProgressDialog.show();
            }
        });
    }

    private void hideProgressDialog(){
        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mProgressDialog != null){
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }

    private void toastFromUIThread(final String message){
        MainCouchDbActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainCouchDbActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
