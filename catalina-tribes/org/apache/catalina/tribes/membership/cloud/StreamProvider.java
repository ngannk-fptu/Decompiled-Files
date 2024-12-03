/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership.cloud;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface StreamProvider {
    public InputStream openStream(String var1, Map<String, String> var2, int var3, int var4) throws IOException;
}

