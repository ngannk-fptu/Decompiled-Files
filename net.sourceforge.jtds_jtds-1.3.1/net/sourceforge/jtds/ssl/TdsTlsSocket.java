/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import net.sourceforge.jtds.ssl.TdsTlsInputStream;
import net.sourceforge.jtds.ssl.TdsTlsOutputStream;

class TdsTlsSocket
extends Socket {
    private final Socket delegate;
    private final InputStream istm;
    private final OutputStream ostm;

    TdsTlsSocket(Socket delegate) throws IOException {
        this.delegate = delegate;
        this.istm = new TdsTlsInputStream(delegate.getInputStream());
        this.ostm = new TdsTlsOutputStream(delegate.getOutputStream());
    }

    @Override
    public synchronized void close() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.istm;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.ostm;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        this.delegate.setSoTimeout(timeout);
    }

    @Override
    public synchronized void setKeepAlive(boolean keepAlive) throws SocketException {
        this.delegate.setKeepAlive(keepAlive);
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        this.delegate.setTcpNoDelay(on);
    }
}

