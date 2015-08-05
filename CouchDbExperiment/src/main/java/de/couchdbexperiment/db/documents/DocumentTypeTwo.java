package de.couchdbexperiment.db.documents;

import java.util.Arrays;
import java.util.List;

/**
 * Created by skip on 25.06.2015.
 */
public class DocumentTypeTwo {
    String property1 = "haha2";
    List<String> props = Arrays.asList("One", "Two");
    List<DocumentTypeOne> propsNested = Arrays.asList(new DocumentTypeOne(), new DocumentTypeOne());
}
