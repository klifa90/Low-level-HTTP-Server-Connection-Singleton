package com.klifa.utils;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;


public class ConexionServidor {

	private static ConexionServidor conexionServidor;

	private ConexionServidor() {
	}

	public static ConexionServidor getInstance() {
		if (conexionServidor == null)
			conexionServidor = new ConexionServidor();
		return conexionServidor;
	}
	
	// BASE URL
	private static final String TAG_BASEURL = "http://myurl.com/ ";

	//METHODS
	//POSTS
	private static final String TAG_POST_METHOD_1 = "post-method";

	//GETS
	private static final String TAG_GET_METHOD_1 = "get-method";
	
	
	private static final int TIMEOUT = 20000; //20 seconds

	
	private HttpClient clientGenerico()
	{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		
		return client;
	}
	
	private HttpPost httpPostConJson(JSONObject json, String url)
	{
		HttpPost request = new HttpPost(url);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		
		try {
			StringEntity se = new StringEntity(json.toString());
			request.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return request;
	}
	
	private HttpGet httpGet(String url) {
        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        return request;
    }
	
	private JSONObject obtenerJsonDeRespuesta(HttpUriRequest request)
	{
		try {
			
		HttpClient client = clientGenerico();
			
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = entity.getContent();
			
			Header encoding = response.getFirstHeader("Content-Encoding");
			if (encoding != null && encoding.getValue().equals("gzip")) {
			    is = new GZIPInputStream(is);
			}
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			String jsonRes = sb.toString();
			JSONObject jObj = new JSONObject(jsonRes);

			return jObj;
		}
		} catch (Exception t) {
			t.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	//Utils methods for hashing strings
	private String hashString(String message, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException{
	 
	        MessageDigest digest = MessageDigest.getInstance(algorithm);
	        byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
	 
	        return convertByteArrayToHexString(hashedBytes);
	}
	
	private String convertByteArrayToHexString(byte[] arrayBytes) {
	    StringBuffer stringBuffer = new StringBuffer();
	    for (int i = 0; i < arrayBytes.length; i++) {
	        stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
	                .substring(1));
	    }
	    return stringBuffer.toString();
	}
	
	//Util method to get int from json
	private int getJSONInt(JSONObject jsonObject, String jsonKey)
    {
        int result = 0;
        try
        {
            result = Integer.parseInt(jsonObject.optString(jsonKey));
        }catch (Exception ex) {}
        return result;
    }
}
