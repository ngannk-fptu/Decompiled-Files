/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.xhtmlrenderer.util.XRLog;

public class StreamResource {
    private final String _uri;
    private URLConnection _conn;
    private int _slen;
    private InputStream _inputStream;

    public StreamResource(String uri) {
        this._uri = uri;
    }

    public void connect() {
        try {
            this._conn = new URL(this._uri).openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(10000));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(30000));
            this._conn.connect();
            this._slen = this._conn.getContentLength();
        }
        catch (MalformedURLException e) {
            XRLog.exception("bad URL given: " + this._uri, e);
        }
        catch (FileNotFoundException e) {
            XRLog.exception("item at URI " + this._uri + " not found");
        }
        catch (IOException e) {
            XRLog.exception("IO problem for " + this._uri, e);
        }
    }

    public boolean hasStreamLength() {
        return this._slen >= 0;
    }

    public int streamLength() {
        return this._slen;
    }

    public BufferedInputStream bufferedStream() throws IOException {
        this._inputStream = this._conn.getInputStream();
        return new BufferedInputStream(this._inputStream);
    }

    public void close() {
        if (this._inputStream != null) {
            try {
                this._inputStream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

