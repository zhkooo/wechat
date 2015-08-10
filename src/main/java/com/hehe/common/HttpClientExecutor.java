package com.hehe.common;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
public class HttpClientExecutor{
    final static HttpClient CLIENT;
	private  HttpClient  httpClient = null;
	private static PoolingHttpClientConnectionManager  connectionManager;
	static{
		LayeredConnectionSocketFactory ssl = null;
        try {
            ssl = SSLConnectionSocketFactory.getSystemSocketFactory();
        } catch (final SSLInitializationException ex) {
            final SSLContext sslcontext;
            try {
                sslcontext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
                sslcontext.init(null, null, null);
                ssl = new SSLConnectionSocketFactory(sslcontext);
            } catch (final SecurityException | NoSuchAlgorithmException | KeyManagementException ignore) {
            }
        }

        final Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", ssl != null ? ssl : SSLConnectionSocketFactory.getSocketFactory())
            .build();

        connectionManager = new PoolingHttpClientConnectionManager(sfr);
        connectionManager.setDefaultMaxPerRoute(100);
        connectionManager.setMaxTotal(200);
        CLIENT = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
	}
    public static HttpClientExecutor newInstance() {
        return new HttpClientExecutor(CLIENT);
    }

    public static HttpClientExecutor newInstance(final HttpClient httpclient) {
        return new HttpClientExecutor(httpclient != null ? httpclient : CLIENT);
    }
    
	HttpClientExecutor(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	public Response execute(HttpRequestBase method){
		int responseCode = -1;
		try {
			HttpResponse httpResponse= httpClient.execute(method);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			Response response = new Response();
			HttpEntity entity = httpResponse.getEntity();  
			response.setResponseAsString(entity!=null?EntityUtils.toString(entity):null);
			response.setStatusCode(responseCode);
			return response;
		} catch(IOException e){
	        throw new RuntimeException(e);
		}finally {
			method.releaseConnection();
		}

	}

}
