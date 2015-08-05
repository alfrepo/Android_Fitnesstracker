package de.couchdbexperiment.db.documents;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.couchdbexperiment.Constants;
import de.couchdbexperiment.Context;

/**
 * Created by skip on 05.06.2015.
 */
public class Document {

    public static final String DOCPROP_CREATED_AT = "created_at";
    public static final String DOCPROP_CREATOR = "creator";
    public static final String DOCPROP_WRITERS = "writers";
    public static final String DOCPROP_TYPE = "type";
    public static final String DOCPROP_SERIALIZED_DATA = "seralizedData";


    public static final void purgeAllDocumentsInLocalDb(){
        Query q = getAllDocsQuery();
        try {
            QueryEnumerator e = q.run();
            for(int i = 0; i<e.getCount(); i++){
                com.couchbase.lite.Document d = (com.couchbase.lite.Document) e.getRow(i).getDocument();
                d.purge();
            }
        } catch (CouchbaseLiteException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Creating the doc
     * @param docProperties - properties for the doc
     * @param docType - class of the document
     */
    public static final void createInDb(Map<String, Object> docProperties, Class docType){
        createInDb(docProperties, docType.getSimpleName());
    }

    /**
     * Creates a document with the given properties.
     * Some required properties will be set automatically.
     * @param docProperties
     */
    public static final void createInDb(Map<String, Object> docProperties, String docType){

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(docProperties);
        properties.put(DOCPROP_TYPE, docType);
        properties.put(DOCPROP_CREATED_AT, currentTimeString);
        properties.put(DOCPROP_CREATOR, Constants.USERNAME);
        properties.put(DOCPROP_WRITERS, String.format("[%s]", Constants.USERNAME));

        com.couchbase.lite.Document document = Context.database.createDocument();

        UnsavedRevision revision = document.createRevision();
        revision.setUserProperties(properties);

        try {
            revision.save();
            Log.d(Constants.TAG, "Created doc: %s", document.getId());

        } catch (CouchbaseLiteException e) {
            Log.d(Constants.TAG, "Creation of doc failed: %s", document.getId());
            e.printStackTrace();
        }
    }

    public static final void createInDb( String docType, String serializedData){
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DOCPROP_SERIALIZED_DATA, serializedData);
        createInDb(properties, docType);
    }

    public static final void createInDb(Object object){
        String serialized = Constants.gson.toJson(object);
    }

    public static final List<Object> getDocuments(Class type){
        return null;
    }



    public static final List<com.couchbase.lite.Document> getAllDocs(){
        Query q = getAllDocsQuery();
        List<com.couchbase.lite.Document> documents = new ArrayList<com.couchbase.lite.Document>();
        try {
            QueryEnumerator e = q.run();
            for(int i = 0; i<e.getCount(); i++){
                com.couchbase.lite.Document d = (com.couchbase.lite.Document) e.getRow(i).getDocument();
                documents.add(d);
            }
        } catch (CouchbaseLiteException e1) {
            e1.printStackTrace();
            Log.e(Constants.TAG, "Failed to retrieve the documents", e1);
        }
        return documents;
    }

    public static final Query getAllDocsQuery(){
        Query query = Context.database.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        query.setDescending(true);

        return query;
    }
}
