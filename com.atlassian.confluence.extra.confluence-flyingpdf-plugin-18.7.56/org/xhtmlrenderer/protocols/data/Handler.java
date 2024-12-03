/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.protocols.data;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.xhtmlrenderer.protocols.data.DataURLConnection;

public class Handler
extends URLStreamHandler {
    @Override
    protected void parseURL(URL u, String spec, int start, int limit) {
        String sub = spec.substring(start, limit);
        if (sub.indexOf(44) < 0) {
            throw new RuntimeException("Improperly formatted data URL");
        }
        this.setURL(u, "data", "", -1, "", "", sub, "", "");
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new DataURLConnection(u);
    }
}

