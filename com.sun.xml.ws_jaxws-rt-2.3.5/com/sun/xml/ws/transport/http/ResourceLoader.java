/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public interface ResourceLoader {
    public URL getResource(String var1) throws MalformedURLException;

    public URL getCatalogFile() throws MalformedURLException;

    public Set<String> getResourcePaths(String var1);
}

