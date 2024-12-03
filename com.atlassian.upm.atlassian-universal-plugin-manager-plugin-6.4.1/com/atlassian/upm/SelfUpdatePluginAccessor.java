/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import java.io.File;
import java.net.URI;

public interface SelfUpdatePluginAccessor {
    public URI prepareUpdate(File var1, String var2, URI var3, URI var4, URI var5);

    public URI getInternalUpdateUri(URI var1);
}

