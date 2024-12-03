/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;

public interface ResourceFactory {
    public Resource generate(String var1, InputStream var2, InputLimit var3) throws IOException;

    public Resource copy(String var1, Resource var2) throws IOException;
}

