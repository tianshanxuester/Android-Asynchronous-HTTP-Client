package com.yfzhang.utils.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

/**
 * Holds all the necessary information for executing an Http request.
 * 
 * @see com.yfzhang.utils.net.HttpClient HttpClient
 * @see com.yfzhang.utils.net.HttpResponse HttpResponse
 * @author yifanz
 */
public class HttpRequest {
	private URL url;
	private String requestMethod;
	private HashMap<String, String> requestHeader;
	private JSONObject body;
	private int readTimeout = 10000;
	private int connTimeout = 10000;
	
	/**
	 * Creates a new HttpRequest instance
	 * 
	 * @param url String
	 * @param method Http request method (GET, POST, etc)
	 * @throws MalformedURLException if the given url String could not be parsed as a URL.
	 */
	public HttpRequest(String url, String method) throws MalformedURLException {
		this.url = new URL(url);
		requestMethod = method;
		requestHeader = new HashMap<String, String>();
	}
	
	/**
	 * Creates a new HttpRequest instance
	 * 
	 * @param url {@link java.net.URL URL} object reference
	 * @param method Http request method (GET, POST, etc)
	 */
	public HttpRequest(URL url, String method) {
		this.url = url;
		requestMethod = method;
		requestHeader = new HashMap<String, String>();
	}
	
	public URL getURL() {
		return url;
	}
	
	public String getRequestMethod() {
		return requestMethod;
	}
	
	/**
	 * Set read timeout in milliseconds (optional)
	 * 
	 * @param readTimeout milliseconds
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	/**
	 * Get read timeout in milliseconds
	 * 
	 * @param readTimeout milliseconds
	 */
	public int getReadTimeout() {
		return readTimeout;
	}
	
	/**
	 * Set connection timeout in milliseconds (optional)
	 * 
	 * @param connTimeout milliseconds
	 */
	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}
	
	/**
	 * Return connection timeout in milliseconds
	 * 
	 * @param connTimeout milliseconds
	 */
	public int getConnTimeout() {
		return connTimeout;
	}
	
	public void putHeaderEntry(String key, String value) {
		requestHeader.put(key, value);
	}
	
	public void putHeaderEntries(Map<String,String> headers) {
		requestHeader.putAll(headers);
	}
	
	public String getHeaderValue(String key) {
		return requestHeader.get(key);
	}
	
	public Iterator<String> getHeaderKeyIterator() {
		return requestHeader.keySet().iterator();
	}
	
	public void setBody(JSONObject json) {
		body = json;
	}
	
	public JSONObject getBody() {
		return body;
	}

}
