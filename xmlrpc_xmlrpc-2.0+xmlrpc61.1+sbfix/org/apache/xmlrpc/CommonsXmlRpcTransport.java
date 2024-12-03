/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.httpclient.Credentials
 *  org.apache.commons.httpclient.Header
 *  org.apache.commons.httpclient.HostConfiguration
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.URI
 *  org.apache.commons.httpclient.UsernamePasswordCredentials
 *  org.apache.commons.httpclient.methods.PostMethod
 */
package org.apache.xmlrpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;

public class CommonsXmlRpcTransport
implements XmlRpcTransport {
    protected PostMethod method;
    private URL url;
    private HttpClient client;
    private final Header userAgentHeader = new Header("User-Agent", "Apache XML-RPC 2.0");
    private boolean http11 = false;
    private boolean gzip = false;
    private boolean rgzip = false;
    private Credentials creds;

    public CommonsXmlRpcTransport(URL url, HttpClient client) {
        HttpClient newClient;
        this.url = url;
        this.client = client == null ? (newClient = new HttpClient()) : client;
    }

    public CommonsXmlRpcTransport(URL url) {
        this(url, null);
    }

    public InputStream sendXmlRpc(byte[] request) throws IOException, XmlRpcClientException {
        String lValue;
        this.method = new PostMethod(this.url.toString());
        this.method.setHttp11(this.http11);
        this.method.setRequestHeader(new Header("Content-Type", "text/xml"));
        if (this.rgzip) {
            this.method.setRequestHeader(new Header("Content-Encoding", "gzip"));
        }
        if (this.gzip) {
            this.method.setRequestHeader(new Header("Accept-Encoding", "gzip"));
        }
        this.method.setRequestHeader(this.userAgentHeader);
        if (this.rgzip) {
            ByteArrayOutputStream lBo = new ByteArrayOutputStream();
            GZIPOutputStream lGzo = new GZIPOutputStream(lBo);
            lGzo.write(request);
            lGzo.finish();
            lGzo.close();
            byte[] lArray = lBo.toByteArray();
            this.method.setRequestBody((InputStream)new ByteArrayInputStream(lArray));
            this.method.setRequestContentLength(-1);
        } else {
            this.method.setRequestBody((InputStream)new ByteArrayInputStream(request));
        }
        URI hostURI = new URI(this.url.toString());
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(hostURI);
        this.client.executeMethod(hostConfig, (HttpMethod)this.method);
        boolean lgzipo = false;
        Header lHeader = this.method.getResponseHeader("Content-Encoding");
        if (lHeader != null && (lValue = lHeader.getValue()) != null) {
            boolean bl = lgzipo = lValue.indexOf("gzip") >= 0;
        }
        if (lgzipo) {
            return new GZIPInputStream(this.method.getResponseBodyAsStream());
        }
        return this.method.getResponseBodyAsStream();
    }

    public void setHttp11(boolean http11) {
        this.http11 = http11;
    }

    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    public void setRGzip(boolean gzip) {
        this.rgzip = gzip;
    }

    public void setUserAgent(String userAgent) {
        this.userAgentHeader.setValue(userAgent);
    }

    public void setBasicAuthentication(String user, String password) {
        this.creds = new UsernamePasswordCredentials(user, password);
        this.client.getState().setCredentials(null, null, this.creds);
    }

    public void endClientRequest() throws XmlRpcClientException {
        this.method.releaseConnection();
    }
}

