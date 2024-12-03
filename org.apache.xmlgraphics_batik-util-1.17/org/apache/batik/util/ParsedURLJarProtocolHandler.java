/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLData;
import org.apache.batik.util.ParsedURLDefaultProtocolHandler;

public class ParsedURLJarProtocolHandler
extends ParsedURLDefaultProtocolHandler {
    public static final String JAR = "jar";

    public ParsedURLJarProtocolHandler() {
        super(JAR);
    }

    @Override
    public ParsedURLData parseURL(ParsedURL baseURL, String urlStr) {
        String start = urlStr.substring(0, JAR.length() + 1).toLowerCase();
        if (start.equals("jar:")) {
            return this.parseURL(urlStr);
        }
        try {
            URL context = new URL(baseURL.toString());
            URL url = new URL(context, urlStr);
            return this.constructParsedURLData(url);
        }
        catch (MalformedURLException mue) {
            return super.parseURL(baseURL, urlStr);
        }
    }
}

