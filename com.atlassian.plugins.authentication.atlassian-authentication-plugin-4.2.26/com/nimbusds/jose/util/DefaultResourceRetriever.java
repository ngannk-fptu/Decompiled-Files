/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.AbstractRestrictedResourceRetriever;
import com.nimbusds.jose.util.BoundedInputStream;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.RestrictedResourceRetriever;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DefaultResourceRetriever
extends AbstractRestrictedResourceRetriever
implements RestrictedResourceRetriever {
    private boolean disconnectAfterUse;
    private Proxy proxy;

    public DefaultResourceRetriever() {
        this(0, 0);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout) {
        this(connectTimeout, readTimeout, 0);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit) {
        this(connectTimeout, readTimeout, sizeLimit, true);
    }

    public DefaultResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit, boolean disconnectAfterUse) {
        super(connectTimeout, readTimeout, sizeLimit);
        this.disconnectAfterUse = disconnectAfterUse;
    }

    public boolean disconnectsAfterUse() {
        return this.disconnectAfterUse;
    }

    public void setDisconnectsAfterUse(boolean disconnectAfterUse) {
        this.disconnectAfterUse = disconnectAfterUse;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Resource retrieveResource(URL url) throws IOException {
        HttpURLConnection con = null;
        try {
            String content;
            con = this.openConnection(url);
            con.setConnectTimeout(this.getConnectTimeout());
            con.setReadTimeout(this.getReadTimeout());
            if (this.getHeaders() != null && !this.getHeaders().isEmpty()) {
                for (Map.Entry<String, List<String>> entry : this.getHeaders().entrySet()) {
                    for (String value : entry.getValue()) {
                        con.addRequestProperty(entry.getKey(), value);
                    }
                }
            }
            InputStream inputStream = this.getInputStream(con, this.getSizeLimit());
            Object object = null;
            try {
                content = IOUtils.readInputStreamToString(inputStream, StandardCharsets.UTF_8);
            }
            catch (Throwable throwable) {
                object = throwable;
                throw throwable;
            }
            finally {
                if (inputStream != null) {
                    if (object != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable) {
                            ((Throwable)object).addSuppressed(throwable);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            }
            int statusCode = con.getResponseCode();
            String statusMessage = con.getResponseMessage();
            if (statusCode > 299 || statusCode < 200) {
                throw new IOException("HTTP " + statusCode + ": " + statusMessage);
            }
            Resource resource = new Resource(content, con.getContentType());
            return resource;
        }
        catch (ClassCastException e) {
            throw new IOException("Couldn't open HTTP(S) connection: " + e.getMessage(), e);
        }
        finally {
            if (this.disconnectAfterUse && con != null) {
                con.disconnect();
            }
        }
    }

    protected HttpURLConnection openConnection(URL url) throws IOException {
        if (this.proxy != null) {
            return (HttpURLConnection)url.openConnection(this.proxy);
        }
        return (HttpURLConnection)url.openConnection();
    }

    private InputStream getInputStream(HttpURLConnection con, int sizeLimit) throws IOException {
        InputStream inputStream = con.getInputStream();
        return sizeLimit > 0 ? new BoundedInputStream(inputStream, this.getSizeLimit()) : inputStream;
    }
}

