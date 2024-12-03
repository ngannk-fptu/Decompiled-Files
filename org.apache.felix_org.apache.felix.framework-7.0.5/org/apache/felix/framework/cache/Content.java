/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public interface Content {
    public void close();

    public boolean hasEntry(String var1);

    public boolean isDirectory(String var1);

    public Enumeration<String> getEntries();

    public byte[] getEntryAsBytes(String var1);

    public InputStream getEntryAsStream(String var1) throws IOException;

    public Content getEntryAsContent(String var1);

    public String getEntryAsNativeLibrary(String var1);

    public URL getEntryAsURL(String var1);

    public long getContentTime(String var1);
}

