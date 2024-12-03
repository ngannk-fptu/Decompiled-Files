/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.url;

import java.net.URL;
import java.net.URLConnection;

public interface URLConnectionHandler {
    public static final String MATCH = "match";

    public void handle(URLConnection var1) throws Exception;

    public boolean matches(URL var1);
}

