/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.url;

import aQute.bnd.service.url.TaggedData;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface URLConnector {
    public InputStream connect(URL var1) throws IOException, Exception;

    public TaggedData connectTagged(URL var1) throws Exception;

    public TaggedData connectTagged(URL var1, String var2) throws Exception;
}

