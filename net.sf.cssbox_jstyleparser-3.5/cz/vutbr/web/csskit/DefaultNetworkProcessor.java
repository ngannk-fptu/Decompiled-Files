/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.NetworkProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class DefaultNetworkProcessor
implements NetworkProcessor {
    @Override
    public InputStream fetch(URL url) throws IOException {
        URLConnection con = url.openConnection();
        InputStream is = "gzip".equalsIgnoreCase(con.getContentEncoding()) ? new GZIPInputStream(con.getInputStream()) : con.getInputStream();
        return is;
    }
}

