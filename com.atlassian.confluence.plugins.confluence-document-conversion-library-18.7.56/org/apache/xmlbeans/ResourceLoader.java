/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.InputStream;

public interface ResourceLoader {
    public InputStream getResourceAsStream(String var1);

    public void close();
}

