package com.yfzhang.utils.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.yfzhang.utils.Constants;
import com.yfzhang.utils.thread.Task;
import com.yfzhang.utils.thread.TaskCallback;
import com.yfzhang.utils.thread.TaskManager;

/**
 * Provides methods for executing {@link com.yfzhang.utils.net.HttpResponse HttpResponse} objects.
 * Must to tied to Activity lifecycle. Remember to call activate(context) in onResume() and shutdown() in onPause methods.
 * @see com.yfzhang.utils.net.HttpRequest HttpRequest
 * @see com.yfzhang.utils.net.HttpResponse HttpResponse
 * @author yifanz
 */
public class HttpClient {
	private static final String TAG = "HttpClient";
	
	static final String GET = "GET";

    static final String POST = "POST";
    
    static final int TIMEOUT = 5000;
    
    private TaskManager<HttpResponse> taskManager;
    
    protected boolean active;
    
    public HttpClient() {
    	active = false;
    }
    
    /**
     * Activate HttpClient. Should be called in onResume() method of Activity.
     * @param context
     * @return true if activation successful, false otherwise
     */
    public boolean activate() {
		if(!active) {
			Handler handler = new Handler();
			taskManager = new TaskManager<HttpResponse>(handler);
			active = true;
			return true;
		} else {
			return false;
		}
	}
    

	/**
	 * Shutdown HttpClient. Should be called in onPause() method of Activity.
	 * @return true if client shutdown, false otherwise
	 */
	public boolean shutdown() {
		if(active) {
			terminateTasks();
			taskManager = null;
			active = false;
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * Synchronous GET
     * 
     * @param serverIP
     * @param postfix
     * @param headers
     * @param body JSONObject body content
     * @return HttpResponse
     */
    public HttpResponse get(String serverIP, String postfix, Map<String,String> headers) {
        HttpResponse response;
        try {
            HttpRequest request = new HttpRequest(serverIP + postfix, GET);
            setupHeaderBody(request, headers, null);
            response = HttpClient.executeRequest(request);
        } catch (MalformedURLException e) {
            response = new HttpResponse(false, 0, null);
        }
        return response;
    }
    
    /**
     * Asynchronous GET
     * 
     * @param id provided by caller
     * @param callback executed when request is returned
     * @param delayMillis delay sending the request
     * @param serverIP
     * @param postfix
     * @param headers
     * @param body JSONObject body content
     */
    public void get(final int id, final TaskCallback<HttpResponse> callback, final long delayMillis, 
    		final String serverIP, final String postfix, final Map<String,String> headers) {
    	Task<HttpResponse> getRequestTask = new Task<HttpResponse>() {
            public void run() {
                HttpResponse response;
                boolean interruption = false;
                try {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    response = get(serverIP, postfix, headers);
                    if (Thread.interrupted())
                        throw new InterruptedException();
                } catch (InterruptedException e) {
                    interruption = true;
                    response = new HttpResponse(false, 0, null);
                }
                if (!interruption)
                    onTaskFinish(id, interruption, response, callback);
            }
        };
        taskManager.updateQueue(getRequestTask, delayMillis);
    }
    
    /**
     * Synchronous POST
     * 
     * @param serverIP
     * @param postfix
     * @param headers
     * @param body
     * @return
     */
    public HttpResponse post(String serverIP, String postfix, Map<String,String> headers, 
    		JSONObject body) {
        HttpResponse response;
        try {
            HttpRequest request = new HttpRequest(serverIP + postfix, POST);
            setupHeaderBody(request, headers, body);
            response = HttpClient.executeRequest(request);
        } catch (MalformedURLException e) {
            response = new HttpResponse(false, 0, null);
        }

        return response;
    }
    
    /**
     * Asynchronous POST
     * 
     * @param id provided by caller
     * @param callback executed when request is returned
     * @param delayMillis delay sending the request
     * @param serverIP
     * @param postfix
     * @param headers
     * @param body JSONObject body content
     */
    public void post(final int id, final TaskCallback<HttpResponse> callback, final long delayMillis, 
    		final String serverIP, final String postfix, final Map<String,String> headers, 
    		final JSONObject body) {
    	Task<HttpResponse> getRequestTask = new Task<HttpResponse>() {
            public void run() {
                HttpResponse response;
                boolean interruption = false;
                try {
                    if (Thread.interrupted())
                        throw new InterruptedException();
                    	response = post(serverIP, postfix, headers, body);
                    if (Thread.interrupted())
                        throw new InterruptedException();
                } catch (InterruptedException e) {
                    interruption = true;
                    response = new HttpResponse(false, 0, null);
                }
                if (!interruption)
                    onTaskFinish(id, interruption, response, callback);
            }
        };
        taskManager.updateQueue(getRequestTask, delayMillis);
    }
    
    /**
     * Sets the request parameters.
     * @param request The request for which the parameters need to be set. 
     * @param headers Headers to be set in request
     * @param body JSON body content
     * @return The request with the set parameters. 
     */
    private HttpRequest setupHeaderBody(HttpRequest request, Map<String,String> headers,
    		JSONObject body) {
        request.setReadTimeout(TIMEOUT);
        request.setConnTimeout(TIMEOUT);
        request.putHeaderEntries(headers);
        request.setBody(body);
        return request;
    }
	
	/**
	 * Execute an Http request.
	 * 
	 * @param request {@link com.yfzhang.utils.net.HttpRequest HttpRequest} object reference
	 * @return {@link com.yfzhang.utils.net.HttpResponse HttpResponse} object reference
	 */
	protected static HttpResponse executeRequest(HttpRequest request) {
		HttpURLConnection conn = null;
		HttpResponse response;
		int respCode = 0;
		try {
			conn = (HttpURLConnection) request.getURL().openConnection();
			
			conn.setRequestMethod(request.getRequestMethod());
			conn.setReadTimeout(request.getReadTimeout());
			conn.setConnectTimeout(request.getConnTimeout());
			conn.setDoInput(true);
			conn.setDoOutput( true );
			
			Iterator<String> iter = request.getHeaderKeyIterator();
			while(iter.hasNext()) {
				String key = iter.next();
				conn.addRequestProperty(key, request.getHeaderValue(key));
			}	
			
			conn.connect();
			
			JSONObject body = request.getBody();
			if(body != null)
				writeBodyData(conn, body);
			
			respCode = conn.getResponseCode();
			String data = readResponseData(conn);
			JSONObject json = null;
			if (data != null)
				json = stringToJSON(data);
			
			response = new HttpResponse(true, respCode, json);
		} catch (IOException e) {
			response = new HttpResponse(false, respCode, null);
			if (Constants.DEBUG)
				Log.i(TAG, "IO Exception during Http execute");
		} catch (JSONException e) {
			response = new HttpResponse(false, respCode, null);
			if (Constants.DEBUG)
				Log.i(TAG, "JSON Exception during Http execute");
		} finally {
			if(conn != null) 
				conn.disconnect();
		}
		return response;
	}
	
	private static String readResponseData(HttpURLConnection conn) throws IOException {	
		String data;
		BufferedReader reader = new BufferedReader( new InputStreamReader(conn.getInputStream(), "UTF-8"));
		data = reader.readLine();
		reader.close();

		return data;
	}
	
	private static void writeBodyData(HttpURLConnection conn, JSONObject body) throws UnsupportedEncodingException, IOException {
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
		writer.write(body.toString());
		writer.close();
	}
	
	private static JSONObject stringToJSON(String s) throws JSONException {
		JSONObject json;
		json = new JSONObject(s);

		return json;
	}
	
	public void terminateTasks() {
    	taskManager.terminateAll();
    }
}
