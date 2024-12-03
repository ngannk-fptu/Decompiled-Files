/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

public interface Jar
extends AutoCloseable {
    public URL getJarFileURL();

    public InputStream getInputStream(String var1) throws IOException;

    public long getLastModified(String var1) throws IOException;

    public boolean exists(String var1) throws IOException;

    @Override
    public void close();

    public void nextEntry();

    public String getEntryName();

    public InputStream getEntryInputStream() throws IOException;

    public String getURL(String var1);

    public Manifest getManifest() throws IOException;

    public void reset() throws IOException;
}

