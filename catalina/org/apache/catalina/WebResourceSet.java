/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

public interface WebResourceSet
extends Lifecycle {
    public WebResource getResource(String var1);

    public String[] list(String var1);

    public Set<String> listWebAppPaths(String var1);

    public boolean mkdir(String var1);

    public boolean write(String var1, InputStream var2, boolean var3);

    public void setRoot(WebResourceRoot var1);

    public boolean getClassLoaderOnly();

    public void setClassLoaderOnly(boolean var1);

    public boolean getStaticOnly();

    public void setStaticOnly(boolean var1);

    public URL getBaseUrl();

    public void setReadOnly(boolean var1);

    public boolean isReadOnly();

    public void gc();
}

