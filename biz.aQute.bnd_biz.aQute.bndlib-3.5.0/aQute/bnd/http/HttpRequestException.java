/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.http;

import aQute.bnd.service.url.TaggedData;
import aQute.lib.io.IO;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpRequestException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public final int responseCode;

    public HttpRequestException(HttpURLConnection conn) throws IOException {
        super(conn.getURL() + ":" + conn.getResponseCode() + ":" + conn.getResponseMessage() == null ? HttpRequestException.getMessage(conn) : conn.getResponseMessage());
        this.responseCode = conn.getResponseCode();
    }

    public HttpRequestException(TaggedData tag) {
        super(tag.getUrl() + ":" + tag.getResponseCode() + ":" + tag.getTag());
        this.responseCode = tag.getResponseCode();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String getMessage(HttpURLConnection conn) {
        try (InputStream in = conn.getErrorStream();){
            if (in == null) return "";
            String string = IO.collect(in);
            return string;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "";
    }
}

