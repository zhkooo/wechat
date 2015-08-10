package com.hehe.weixin.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hehe.common.HttpClientRequest;
import com.tencent.common.Configure;

public class WeiXinHttpClientRequest extends HttpClientRequest {
	private static Logger log = LoggerFactory.getLogger(WeiXinHttpClientRequest.class);
	private static HttpClient httpClient = null;
	static {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream instream = new FileInputStream(new File(Configure.getCertLocalPath()));// 加载本地的证书进行https加密传输
			keyStore.load(instream, Configure.getCertPassword().toCharArray());// 设置证书密码
		} catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException e) {
			log.error("加载https的证书秘钥失败", e);
		}
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContexts
					.custom()
					.loadKeyMaterial(keyStore,
							Configure.getCertPassword().toCharArray()).build();
		} catch (KeyManagementException | UnrecoverableKeyException| NoSuchAlgorithmException | KeyStoreException e) {
			log.error("初始化https的ssl失败", e);
		}
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	public WeiXinHttpClientRequest() {
		super();
		setHttpClient(httpClient);
	}

}
