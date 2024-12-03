/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.fit.net;

import cz.vutbr.web.css.CSSFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import org.fit.net.Base64Coder;
import org.fit.net.DataURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataURLHandler
extends URLStreamHandler {
    private static final Logger log = LoggerFactory.getLogger(CSSFactory.class);
    protected String mime = "text/plain";
    protected String charset = "US-ASCII";
    protected boolean encoded = false;

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        if ("data".equals(u.getProtocol())) {
            byte[] bytes;
            String data;
            String path = u.getPath();
            if (path == null || path.isEmpty()) {
                throw new IOException("No data specified");
            }
            String[] parts = path.split(",", 2);
            if (parts.length == 2) {
                String[] hparts;
                data = parts[1];
                for (String part : hparts = parts[0].split(";", 3)) {
                    if (part.equalsIgnoreCase("base64")) {
                        this.encoded = true;
                        continue;
                    }
                    if (part.startsWith("charset=")) {
                        this.charset = part.substring(8);
                        continue;
                    }
                    this.mime = part;
                }
            } else {
                data = parts[0];
            }
            if (!this.encoded) {
                bytes = URLDecoder.decode(data, this.charset).getBytes(this.charset);
            } else {
                try {
                    bytes = Base64Coder.decode(data);
                }
                catch (Exception e) {
                    throw new IOException("Couldn't decode base64 data", e);
                }
            }
            return new DataURLConnection(u, this.mime, this.charset, bytes);
        }
        throw new IOException("Only the 'data' protocol is supported by this URL handler");
    }

    public static URL createURL(URL base, String urlstring) throws MalformedURLException {
        if (urlstring.startsWith("data:")) {
            return new URL(null, urlstring, new DataURLHandler());
        }
        URL ret = new URL(base, urlstring);
        String path = ret.getPath();
        if (path.startsWith("/./") || path.startsWith("/../")) {
            path = path.substring(1);
            while (path.startsWith("./") || path.startsWith("../")) {
                if (path.startsWith("./")) {
                    path = path.substring(2);
                    continue;
                }
                path = path.substring(3);
            }
            URL fixed = new URL(base, "/" + path);
            log.warn("Normalized non-standard URL %s to %s", (Object)ret.toString(), (Object)fixed.toString());
            ret = fixed;
        }
        return ret;
    }
}

