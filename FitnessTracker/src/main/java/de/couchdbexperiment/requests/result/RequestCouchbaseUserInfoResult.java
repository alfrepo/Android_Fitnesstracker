package de.couchdbexperiment.requests.result;

import java.util.ArrayList;

/**
 * Login Request.
 * It returns the Session Object: RequestCouchbaseAuthenticationResult.class
 * Created by skip on 04.06.2015.
 */
public class RequestCouchbaseUserInfoResult {

    public String name;
    public ArrayList<String> admin_channels;
    public ArrayList<String> all_channels;
    public ArrayList<String> admin_roles;
    public ArrayList<String> roles;

}
