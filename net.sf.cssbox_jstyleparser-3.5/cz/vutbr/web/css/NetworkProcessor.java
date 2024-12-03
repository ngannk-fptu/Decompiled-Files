/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface NetworkProcessor {
    public InputStream fetch(URL var1) throws IOException;
}

