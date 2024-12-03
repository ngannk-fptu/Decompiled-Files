/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.protocols.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.protocols.data.Base64;
import org.xhtmlrenderer.protocols.data.URLByteDecoder;

public class DataURLConnection
extends URLConnection {
    private Map _headers = new HashMap();
    private byte[] _data;

    DataURLConnection(URL u) {
        super(u);
    }

    @Override
    public void connect() throws IOException {
        this.parseURL();
    }

    @Override
    public String getContentType() {
        String type = (String)this._headers.get("Content-Type");
        if (type == null) {
            return "Content-Type: text/plain; charset=US-ASCII";
        }
        return type;
    }

    @Override
    public int getContentLength() {
        if (this._data == null) {
            return 0;
        }
        return this._data.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        if (this._data == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(this._data);
    }

    protected void parseURL() throws UnsupportedEncodingException {
        String charset;
        String[] parts;
        String sub = this.getURL().getPath();
        int comma = sub.indexOf(44);
        if (comma < 0) {
            throw new RuntimeException("Improperly formatted data URL");
        }
        String meta = sub.substring(0, comma);
        String data = sub.substring(comma + 1);
        boolean isBase64 = false;
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("charset", "US-ASCII");
        if (meta.length() > 0 && (parts = meta.split(";")).length > 0) {
            int index = 0;
            if (meta.charAt(0) != ';') {
                this._headers.put("Content-Type", parts[index++]);
            }
            while (index < parts.length) {
                if (parts[index].indexOf("=") >= 0) {
                    String[] nameValuePair = parts[index].split("=");
                    if (nameValuePair.length > 1) {
                        this._headers.put(nameValuePair[0], nameValuePair[1]);
                    }
                } else if (parts[index].compareTo("base64") == 0) {
                    isBase64 = true;
                }
                ++index;
            }
        }
        if (!Charset.isSupported(charset = (String)properties.get("charset"))) {
            throw new UnsupportedCharsetException(charset);
        }
        this._data = isBase64 ? Base64.decode(data) : URLByteDecoder.decode(data);
    }
}

