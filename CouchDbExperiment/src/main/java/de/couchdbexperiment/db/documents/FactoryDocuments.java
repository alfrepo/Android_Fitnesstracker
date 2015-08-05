package de.couchdbexperiment.db.documents;

import java.util.Map;

/**
 * Created by skip on 25.06.2015.
 */
public class FactoryDocuments {

    public static final String typeTwo = "a";


    public static Object createDocumentByType(Map<String, Object> properties){
        return null;
    }

    public static Object createDocumentByType(Map<String, Object> properties, final String docType){

        if(DocumentTypeOne.class.getSimpleName().equals(docType)){

        }

        throw new IllegalArgumentException("Failed to convert " + docType);
    }
}
