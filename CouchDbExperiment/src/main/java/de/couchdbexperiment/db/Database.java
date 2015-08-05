package de.couchdbexperiment.db;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;

import de.couchdbexperiment.Constants;

/**
 * Created by skip on 21.06.2015.
 */
public class Database {

    public static com.couchbase.lite.Database addDatabaseToContext(Context context) {
        Manager manager;
        try {

            Manager.enableLogging(Constants.TAG, Log.VERBOSE);
            Manager.enableLogging(Log.TAG, Log.VERBOSE);
            Manager.enableLogging(Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
            Manager.enableLogging(Log.TAG_SYNC, Log.VERBOSE);
            Manager.enableLogging(Log.TAG_QUERY, Log.VERBOSE);
            Manager.enableLogging(Log.TAG_VIEW, Log.VERBOSE);
            Manager.enableLogging(Log.TAG_DATABASE, Log.VERBOSE);

            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(Constants.TAG, "Cannot create Manager object", e);
            return null;
        }

        try {
            de.couchdbexperiment.Context.database = manager.getDatabase(Constants.DATABASE_NAME);
            return de.couchdbexperiment.Context.database;

        } catch (CouchbaseLiteException e) {
            Log.e(Constants.TAG, "Cannot get Database", e);
            return null;
        }
    }


    public static void deleteDatabaseFromContext(){
        try {
            if(de.couchdbexperiment.Context.database != null){
                de.couchdbexperiment.Context.database.close();
                de.couchdbexperiment.Context.database.delete();
                de.couchdbexperiment.Context.database = null;
            }

        } catch (CouchbaseLiteException e) {
            Log.e(Constants.TAG, "Failed to create the Database", e);
        }
    }
}
