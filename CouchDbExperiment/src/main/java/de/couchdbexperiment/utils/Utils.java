package de.couchdbexperiment.utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by skip on 04.06.2015.
 */
public class Utils {

    /**
     * Creates a Request body of the form:
     * {
     *  "key" : "value",
     *  "key" : "value"
     * }
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getRestRequestBody(Map.Entry<String, String>... params)
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;


        for (Map.Entry<String,String> pair : params)
        {
            result.append("\"");
            result.append(pair.getKey());
            result.append("\"");

            result.append(":");

            // do not wrap archives with quotes
            if(pair.getValue()!=null && !pair.getValue().startsWith("[")){
                result.append("\"");
                result.append(pair.getValue());
                result.append("\"");
            }else{
                result.append(pair.getValue());
            }


            result.append(",");
        }

        //delete last ,
        if(result.length()>0){
            result.deleteCharAt(result.length()-1);
        }

        // wrap {}
        result.insert(0, "{");
        result.append("}");

        return result.toString();
    }
}
