/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpURLConnection
extends java.net.HttpURLConnection {
    private static final Log LOG = LogFactory.getLog(HttpURLConnection.class);
    private HttpMethod method;
    private URL url;

    public HttpURLConnection(HttpMethod method, URL url) {
        super(url);
        this.method = method;
        this.url = url;
    }

    protected HttpURLConnection(URL url) {
        super(url);
        throw new RuntimeException("An HTTP URL connection can only be constructed from a HttpMethod class");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getInputStream()");
        return this.method.getResponseBodyAsStream();
    }

    @Override
    public InputStream getErrorStream() {
        LOG.trace((Object)"enter HttpURLConnection.getErrorStream()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void disconnect() {
        LOG.trace((Object)"enter HttpURLConnection.disconnect()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void connect() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.connect()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean usingProxy() {
        LOG.trace((Object)"enter HttpURLConnection.usingProxy()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String getRequestMethod() {
        LOG.trace((Object)"enter HttpURLConnection.getRequestMethod()");
        return this.method.getName();
    }

    @Override
    public int getResponseCode() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getResponseCode()");
        return this.method.getStatusCode();
    }

    @Override
    public String getResponseMessage() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getResponseMessage()");
        return this.method.getStatusText();
    }

    @Override
    public String getHeaderField(String name) {
        LOG.trace((Object)"enter HttpURLConnection.getHeaderField(String)");
        Header[] headers = this.method.getResponseHeaders();
        for (int i = headers.length - 1; i >= 0; --i) {
            if (!headers[i].getName().equalsIgnoreCase(name)) continue;
            return headers[i].getValue();
        }
        return null;
    }

    @Override
    public String getHeaderFieldKey(int keyPosition) {
        LOG.trace((Object)"enter HttpURLConnection.getHeaderFieldKey(int)");
        if (keyPosition == 0) {
            return null;
        }
        Header[] headers = this.method.getResponseHeaders();
        if (keyPosition < 0 || keyPosition > headers.length) {
            return null;
        }
        return headers[keyPosition - 1].getName();
    }

    @Override
    public String getHeaderField(int position) {
        LOG.trace((Object)"enter HttpURLConnection.getHeaderField(int)");
        if (position == 0) {
            return this.method.getStatusLine().toString();
        }
        Header[] headers = this.method.getResponseHeaders();
        if (position < 0 || position > headers.length) {
            return null;
        }
        return headers[position - 1].getValue();
    }

    @Override
    public URL getURL() {
        LOG.trace((Object)"enter HttpURLConnection.getURL()");
        return this.url;
    }

    @Override
    public void setInstanceFollowRedirects(boolean isFollowingRedirects) {
        LOG.trace((Object)"enter HttpURLConnection.setInstanceFollowRedirects(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        LOG.trace((Object)"enter HttpURLConnection.getInstanceFollowRedirects()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        LOG.trace((Object)"enter HttpURLConnection.setRequestMethod(String)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public Permission getPermission() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getPermission()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Object getContent() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getContent()");
        throw new RuntimeException("Not implemented yet");
    }

    public Object getContent(Class[] classes) throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getContent(Class[])");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        LOG.trace((Object)"enter HttpURLConnection.getOutputStream()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public void setDoInput(boolean isInput) {
        LOG.trace((Object)"enter HttpURLConnection.setDoInput()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean getDoInput() {
        LOG.trace((Object)"enter HttpURLConnection.getDoInput()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setDoOutput(boolean isOutput) {
        LOG.trace((Object)"enter HttpURLConnection.setDoOutput()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean getDoOutput() {
        LOG.trace((Object)"enter HttpURLConnection.getDoOutput()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setAllowUserInteraction(boolean isAllowInteraction) {
        LOG.trace((Object)"enter HttpURLConnection.setAllowUserInteraction(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean getAllowUserInteraction() {
        LOG.trace((Object)"enter HttpURLConnection.getAllowUserInteraction()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setUseCaches(boolean isUsingCaches) {
        LOG.trace((Object)"enter HttpURLConnection.setUseCaches(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public boolean getUseCaches() {
        LOG.trace((Object)"enter HttpURLConnection.getUseCaches()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setIfModifiedSince(long modificationDate) {
        LOG.trace((Object)"enter HttpURLConnection.setIfModifiedSince(long)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public long getIfModifiedSince() {
        LOG.trace((Object)"enter HttpURLConnection.getIfmodifiedSince()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public boolean getDefaultUseCaches() {
        LOG.trace((Object)"enter HttpURLConnection.getDefaultUseCaches()");
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void setDefaultUseCaches(boolean isUsingCaches) {
        LOG.trace((Object)"enter HttpURLConnection.setDefaultUseCaches(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public void setRequestProperty(String key, String value) {
        LOG.trace((Object)"enter HttpURLConnection.setRequestProperty()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }

    @Override
    public String getRequestProperty(String key) {
        LOG.trace((Object)"enter HttpURLConnection.getRequestProperty()");
        throw new RuntimeException("Not implemented yet");
    }
}

