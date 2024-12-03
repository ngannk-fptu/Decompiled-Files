/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.bouncycastle.est.LimitedSource;
import org.bouncycastle.est.Source;
import org.bouncycastle.est.TLSUniqueProvider;
import org.bouncycastle.est.jcajce.ChannelBindingProvider;

class LimitedSSLSocketSource
implements Source<SSLSession>,
TLSUniqueProvider,
LimitedSource {
    protected final SSLSocket socket;
    private final ChannelBindingProvider bindingProvider;
    private final Long absoluteReadLimit;

    public LimitedSSLSocketSource(SSLSocket sock, ChannelBindingProvider bindingProvider, Long absoluteReadLimit) {
        this.socket = sock;
        this.bindingProvider = bindingProvider;
        this.absoluteReadLimit = absoluteReadLimit;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    @Override
    public SSLSession getSession() {
        return this.socket.getSession();
    }

    @Override
    public byte[] getTLSUnique() {
        if (this.isTLSUniqueAvailable()) {
            return this.bindingProvider.getChannelBinding(this.socket, "tls-unique");
        }
        throw new IllegalStateException("No binding provider.");
    }

    @Override
    public boolean isTLSUniqueAvailable() {
        return this.bindingProvider.canAccessChannelBinding(this.socket);
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

    @Override
    public Long getAbsoluteReadLimit() {
        return this.absoluteReadLimit;
    }
}

