
package com.yfzhang.utils.net;

import static com.yfzhang.utils.net.ExampleClientConstants.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yfzhang.utils.Base64;
import com.yfzhang.utils.Constants;
import com.yfzhang.utils.thread.TaskCallback;

/**
 * An example class that demonstrates how to use the HttpClient.
 * 
 * Provides methods for requesting server services synchronously
 * and asynchronously. Must to tied to Activity lifecycle. Remember to call
 * activate(context) in onResume() and shutdown() in onPause methods.
 * 
 * @author yifanz
 */
public class ExampleClient extends HttpClient {
    private static final String TAG = "ExampleClient";
    
    public static final int HTTP_SUCCESSFUL = 200;

    /**
     * Creates a new client instance.
     */
    public ExampleClient() {
        super();
    }

    /**
     * Synchronous authenticated GET
     * 
     * @param serverIP
     * @param postfix
     * @param authKey Base64 encoded authentication key
     * @param headers
     * @return
     */
    protected HttpResponse authenticatedGet(String serverIP, String postfix, String authKey,
            Map<String, String> headers) {
        setAuthenticatedHeader(headers, authKey);
        HttpResponse response = super.get(serverIP, postfix, headers);

        return response;
    }

    /**
     * Asynchronous authenticated GET
     * 
     * @param id provided by caller
     * @param callback executed when request is returned
     * @param delayMillis delay sending the request
     * @param serverIP
     * @param postfix
     * @param authKey Base64 encoded authentication key
     * @param headers
     */
    protected void authenticatedGet(final int id, final TaskCallback<HttpResponse> callback,
            final long delayMillis, final String serverIP, final String postfix,
            final String authKey, final Map<String, String> headers) {
        setAuthenticatedHeader(headers, authKey);
        super.get(id, callback, delayMillis, serverIP, postfix, headers);
    }

    /**
     * Synchronous authenticated POST
     * 
     * @oaram serverIP
     * @param postfix
     * @param authKey Base64 encoded authentication key
     * @param headers
     * @param body
     * @return
     */
    protected HttpResponse authenticatedPost(String serverIP, String postfix, String authKey,
            Map<String, String> headers, JSONObject body) {
        setAuthenticatedHeader(headers, authKey);
        HttpResponse response = super.post(serverIP, postfix, headers, body);
        return response;
    }

    /**
     * Asynchronous authenticated POST
     * 
     * @param id provided by caller
     * @param callback executed when request is returned
     * @param delayMillis delay sending the request
     * @param serverIP
     * @param postfix
     * @param authKey Base64 encoded authentication key
     * @param headers
     * @param body JSONObject body content
     */
    protected void authenticatedPost(final int id, final TaskCallback<HttpResponse> callback,
            final long delayMillis, final String serverIP, final String postfix,
            final String authKey, final Map<String, String> headers, final JSONObject body) {
        setAuthenticatedHeader(headers, authKey);
        super.post(id, callback, delayMillis, serverIP, postfix, headers, body);
    }

    public static String makeAuthKey(String user, String pass) {
        String s = user + ":" + pass;
        return Base64.encodeBytes(s.getBytes());
    }

    /**
     * Method call to set request authentication params. Adds authentication to header.
     * @param Map<String,String> http header
     * @param key the base64 encoded key
     * @return The reqest with authentication parameters
     */
    private void setAuthenticatedHeader(Map<String, String> header, String key) {
        header.put(AUTHORIZATION, BASIC + key);
    }

    /**
     * Turn a Map of params into a String that can be embedded in URL
     * 
     * @param params Map<String, String> of params key-values
     */
    private String makeParamString(Map<String, String> params) {
        String paramString = "?";
        Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            paramString += key + "=";
            paramString += params.get(key) + "&";
        }
        paramString = paramString.substring(0, paramString.length() - 1);
        return paramString;
    }

    /**
     * Verify User Credentials
     * <p>Synchronous</p>
     * <p>Authenticated</p>
     * @param authKey
     * @return
     */
    public HttpResponse verifyUserCredentials(String authKey) {
        HttpResponse response = authenticatedGet(SERVER_IP, METHOD_VERIFY_USER_CREDENTIALS,
                authKey, new HashMap<String, String>());
        return response;
    }

    /**
     * Verify User Credentials
     * <p>Asynchronous</p>
     * <p>Authenticated</p>
     * @param authKey
     * @param id
     * @param callback
     * @param delayMillis
     */
    public void verifyUserCredentials(String authKey, int id, TaskCallback<HttpResponse> callback,
            int delayMillis) {
        authenticatedGet(id, callback, delayMillis, SERVER_IP, METHOD_VERIFY_USER_CREDENTIALS,
                authKey, new HashMap<String, String>());
    }

    /**
     * Geographic Coordinate Location Search
     * <p>Asynchronous</p>
     * <p>Not Authenticated</p>
     * 
     * @param lat latitude
     * @param lon longitude
     * @param radius search radius in miles 
     * @param what
     */
    public void gcs(String lat, String lon, String radius, String what, int id,
            TaskCallback<HttpResponse> callback, int delayMillis) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(LATITUDE, lat);
        params.put(LONGITUDE, lon);
        params.put(RADIUS, radius);
        params.put(WHAT, what);
        String paramString = makeParamString(params);
        get(id, callback, delayMillis, SERVER_IP, METHOD_GCS + paramString,
                new HashMap<String, String>());
    }
    
    /**
     * Create New User
     * <p>Synchronous</p>
     * <p>Not Authenticated</p>
     * @param email
     * @param fname
     * @param lname
     * @param pass
     * @param profilePicID optional
     * @param about optional
     * @param hometown optional
     * @return HttpResponse
     */
    @Deprecated
    public HttpResponse newUser(String email, String fname, String lname, String pass,
    		String facebookUID, String profilePicID, String about, String hometown) {
    	JSONObject body = new JSONObject();
    	try {
    		body.put(EMAIL, email);
    		body.put(PASSWORD, pass);
			body.put(FIRSTNAME, fname);
			body.put(LASTNAME, lname);
			if (facebookUID != null)
				body.put(FACEBOOKUID, facebookUID);
			if (profilePicID != null)
				body.put(PROFILEPICID, profilePicID);
			if (hometown != null)
				body.put(HOMETOWN, hometown);
			if (about != null)
				body.put(USERABOUT, about);	
		} catch (JSONException e) {
			if (Constants.DEBUG) 
				Log.i(TAG, "newUser() JSONexception in request body");
		}
		Map<String, String> headers = new HashMap<String,String>();
		headers.put(CONTENT_TYPE, APPLICATION_JSON);
		HttpResponse response = post(SERVER_IP, METHOD_USERS, headers, body);
		return response;
    }
    
    /**
     * Create New Taggstr User
     * <p>Asynchronous</p>
     * <p>Not Authenticated</p>
     * @param email
     * @param fname
     * @param lname
     * @param pass
     * @param facebookUID
     * @param profilePicID
     * @param about
     * @param hometown
     * @param id
     * @param callback
     * @param delayMillis
     */
    @Deprecated
    public void newUser(String email, String fname, String lname, String pass,
    		String facebookUID, String profilePicID, String about, String hometown,
    		int id, TaskCallback<HttpResponse> callback, int delayMillis) {
    	JSONObject body = new JSONObject();
    	try {
    		body.put(EMAIL, email);
    		body.put(PASSWORD, pass);
			body.put(FIRSTNAME, fname);
			body.put(LASTNAME, lname);
			if (facebookUID != null)
				body.put(FACEBOOKUID, facebookUID);
			if (profilePicID != null)
				body.put(PROFILEPICID, profilePicID);
			if (hometown != null)
				body.put(HOMETOWN, hometown);
			if (about != null)
				body.put(USERABOUT, about);	
		} catch (JSONException e) {
			if (Constants.DEBUG) 
				Log.i(TAG, "newUser() JSONexception in request body");
		}
		Map<String, String> headers = new HashMap<String,String>();
		headers.put(CONTENT_TYPE, APPLICATION_JSON);
    	post(id, callback, delayMillis, SERVER_IP, METHOD_USERS, headers, body);
    }
}