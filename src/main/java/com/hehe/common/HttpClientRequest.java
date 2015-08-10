package com.hehe.common;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
public class HttpClientRequest{
	private HttpClient  httpClient = HttpClientExecutor.CLIENT;
	private HttpRequestBase request = null;
    private Integer socketTmeout = 30*1000;
    private Integer connectTimeout = 30*1000;
    
    public HttpClientRequest get(String url){
		HttpGet httpGet = new HttpGet(url);
		return new HttpClientRequest(httpGet);
	}
    
    public  HttpClientRequest post(String url,String requestStr)  {
		HttpPost httpPost = new HttpPost(url);
		return new HttpClientRequest(httpPost);
	}
    
    public  HttpClientRequest postStr(String url,String requestStr,ContentType contentType)  {
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = null;
		entity = new StringEntity(requestStr,contentType);
		httpPost.setEntity(entity);
		return new HttpClientRequest(httpPost);
	}
    
    public HttpClientRequest postXml(String url,String requestStr)  {
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = null;
		entity = new StringEntity(requestStr,ContentType.APPLICATION_XML);
		httpPost.setEntity(entity);
		return new HttpClientRequest(httpPost);
	}
    
    public HttpClientRequest postJson(String url,String requestStr)  {
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = null;
		entity = new StringEntity(requestStr,ContentType.APPLICATION_JSON);
		httpPost.setEntity(entity);
		return new HttpClientRequest(httpPost);
	}
    
 
	public HttpClientRequest(){}
    
    public HttpClientRequest(HttpRequestBase request) {
        this.request = request;
    }
    
    public Response execute( HttpContext localContext){
        final RequestConfig.Builder builder;
        if (httpClient instanceof Configurable) {
            builder = RequestConfig.copy(((Configurable) httpClient).getConfig());
        } else {
            builder = RequestConfig.custom();
        }
        if (this.socketTmeout != null) {
            builder.setSocketTimeout(this.socketTmeout);
        }
        if (this.connectTimeout != null) {
            builder.setConnectTimeout(this.connectTimeout);
        }
        final RequestConfig config = builder.build();
        this.request.setConfig(config);
        return HttpClientExecutor.newInstance(httpClient).execute(request);
    }

    public Response execute(){
    	return execute(null);
    }
    
    public HttpClientRequest setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}
    
    public HttpClientRequest setHttpRequestBase(HttpRequestBase request) {
    	this.request = request;
		return this;
	}

    public HttpClientRequest socketTimeout(final int timeout) {
        this.socketTmeout = timeout;
        return this;
    }

    public HttpClientRequest connectTimeout(final int timeout) {
        this.connectTimeout = timeout;
        return this;
    }
    
    public HttpClientRequest addHeader(final Header header) {
        this.request.addHeader(header);
        return this;
    }

    public HttpClientRequest setHeader(final Header header) {
        this.request.setHeader(header);
        return this;
    }

    public HttpClientRequest addHeader(final String name, final String value) {
        this.request.addHeader(name, value);
        return this;
    }

    public HttpClientRequest setHeader(final String name, final String value) {
        this.request.setHeader(name, value);
        return this;
    }

    public HttpClientRequest removeHeader(final Header header) {
        this.request.removeHeader(header);
        return this;
    }

    public HttpClientRequest removeHeaders(final String name) {
        this.request.removeHeaders(name);
        return this;
    }

    public HttpClientRequest setHeaders(final Header... headers) {
        this.request.setHeaders(headers);
        return this;
    }
    
    public HttpClientRequest body(final HttpEntity entity) {
        if (this.request instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) this.request).setEntity(entity);
        } else {
            throw new IllegalStateException(this.request.getMethod()
                    + " request cannot enclose an entity");
        }
        return this;
    }

   public HttpClientRequest bodyForm(final Iterable <? extends NameValuePair> formParams, final Charset charset) {
       final List<NameValuePair> paramList = new ArrayList<>();
        for (NameValuePair param : formParams) {
            paramList.add(param);
        }
        final ContentType contentType = ContentType.create(URLEncodedUtils.CONTENT_TYPE, charset);
        final String s = URLEncodedUtils.format(paramList, charset != null ? charset.name() : null);
        return bodyString(s, contentType);
    }

    public HttpClientRequest bodyForm(final Iterable <? extends NameValuePair> formParams) {
        return bodyForm(formParams, Consts.ISO_8859_1);
    }

    public HttpClientRequest bodyForm(final NameValuePair... formParams) {
        return bodyForm(Arrays.asList(formParams), Consts.ISO_8859_1);
    }

    public HttpClientRequest bodyString(final String s, final ContentType contentType) {
        return body(new StringEntity(s, contentType));
    }

    public HttpClientRequest bodyFile(final File file, final ContentType contentType) {
        return body(new FileEntity(file, contentType));
    }

    public HttpClientRequest bodyByteArray(final byte[] b) {
        return body(new ByteArrayEntity(b));
    }

    public HttpClientRequest bodyByteArray(final byte[] b, final ContentType contentType) {
        return body(new ByteArrayEntity(b, contentType));
    }

    public HttpClientRequest bodyByteArray(final byte[] b, final int off, final int len) {
        return body(new ByteArrayEntity(b, off, len));
    }

    public HttpClientRequest bodyByteArray(final byte[] b, final int off, final int len, final ContentType contentType) {
        return body(new ByteArrayEntity(b, off, len, contentType));
    }

    public HttpClientRequest bodyStream(final InputStream instream) {
        return body(new InputStreamEntity(instream, -1, null));
    }

    public HttpClientRequest bodyStream(final InputStream instream, final ContentType contentType) {
        return body(new InputStreamEntity(instream, -1, contentType));
    }

	

}
