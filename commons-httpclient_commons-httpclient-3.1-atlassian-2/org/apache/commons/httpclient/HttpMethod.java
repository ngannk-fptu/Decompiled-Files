/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.params.HttpMethodParams;

public interface HttpMethod {
    public String getName();

    public HostConfiguration getHostConfiguration();

    public void setPath(String var1);

    public String getPath();

    public URI getURI() throws URIException;

    public void setURI(URI var1) throws URIException;

    public void setStrictMode(boolean var1);

    public boolean isStrictMode();

    public void setRequestHeader(String var1, String var2);

    public void setRequestHeader(Header var1);

    public void addRequestHeader(String var1, String var2);

    public void addRequestHeader(Header var1);

    public Header getRequestHeader(String var1);

    public void removeRequestHeader(String var1);

    public void removeRequestHeader(Header var1);

    public boolean getFollowRedirects();

    public void setFollowRedirects(boolean var1);

    public void setQueryString(String var1);

    public void setQueryString(NameValuePair[] var1);

    public String getQueryString();

    public Header[] getRequestHeaders();

    public Header[] getRequestHeaders(String var1);

    public boolean validate();

    public int getStatusCode();

    public String getStatusText();

    public Header[] getResponseHeaders();

    public Header getResponseHeader(String var1);

    public Header[] getResponseHeaders(String var1);

    public Header[] getResponseFooters();

    public Header getResponseFooter(String var1);

    public byte[] getResponseBody() throws IOException;

    public String getResponseBodyAsString() throws IOException;

    public InputStream getResponseBodyAsStream() throws IOException;

    public boolean hasBeenUsed();

    public int execute(HttpState var1, HttpConnection var2) throws HttpException, IOException;

    public void abort();

    public void recycle();

    public void releaseConnection();

    public void addResponseFooter(Header var1);

    public StatusLine getStatusLine();

    public boolean getDoAuthentication();

    public void setDoAuthentication(boolean var1);

    public HttpMethodParams getParams();

    public void setParams(HttpMethodParams var1);

    public AuthState getHostAuthState();

    public AuthState getProxyAuthState();

    public boolean isRequestSent();
}

