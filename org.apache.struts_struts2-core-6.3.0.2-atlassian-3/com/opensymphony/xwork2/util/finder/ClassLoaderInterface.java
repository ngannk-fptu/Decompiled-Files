/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.finder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public interface ClassLoaderInterface {
    public static final String CLASS_LOADER_INTERFACE = "__current_class_loader_interface";

    public Class<?> loadClass(String var1) throws ClassNotFoundException;

    public URL getResource(String var1);

    public Enumeration<URL> getResources(String var1) throws IOException;

    public InputStream getResourceAsStream(String var1) throws IOException;

    public ClassLoaderInterface getParent();
}

