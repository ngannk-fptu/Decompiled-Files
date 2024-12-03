/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lesscss;

import java.io.IOException;
import java.net.URI;

public interface Loader {
    public URI resolve(URI var1, String var2);

    public String load(URI var1) throws IOException;

    public String dataUri(String var1, URI var2) throws IOException;
}

