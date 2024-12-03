/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.catalina.tribes.membership.cloud.AbstractStreamProvider;

public class TokenStreamProvider
extends AbstractStreamProvider {
    private String token;
    private SSLSocketFactory factory;

    TokenStreamProvider(String token, String caCertFile) throws Exception {
        this.token = token;
        TrustManager[] trustManagers = TokenStreamProvider.configureCaCert(caCertFile);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, null);
        this.factory = context.getSocketFactory();
    }

    @Override
    protected SSLSocketFactory getSocketFactory() {
        return this.factory;
    }

    @Override
    public InputStream openStream(String url, Map<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        if (this.token != null) {
            headers.put("Authorization", "Bearer " + this.token);
        }
        try {
            return super.openStream(url, headers, connectTimeout, readTimeout);
        }
        catch (IOException e) {
            throw new IOException(sm.getString("tokenStream.failedConnection", url, this.token), e);
        }
    }
}

