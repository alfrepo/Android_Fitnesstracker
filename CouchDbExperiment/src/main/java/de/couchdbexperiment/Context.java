package de.couchdbexperiment;

import com.couchbase.lite.Database;

import de.couchdbexperiment.db.Synchronize;

/**
 * Created by skip on 05.06.2015.
 */
public class Context {
    // android contxt
    public static android.content.Context androidContext;

    //the database to which the app is connected
    public static Database database;

    // object which starts and stops the replication
    public static Synchronize synchronize;

}
