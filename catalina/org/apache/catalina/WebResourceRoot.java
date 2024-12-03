/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.TrackedWebResource;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceSet;

public interface WebResourceRoot
extends Lifecycle {
    public WebResource getResource(String var1);

    public WebResource[] getResources(String var1);

    public WebResource getClassLoaderResource(String var1);

    public WebResource[] getClassLoaderResources(String var1);

    public String[] list(String var1);

    public Set<String> listWebAppPaths(String var1);

    public WebResource[] listResources(String var1);

    public boolean mkdir(String var1);

    public boolean write(String var1, InputStream var2, boolean var3);

    public void createWebResourceSet(ResourceSetType var1, String var2, URL var3, String var4);

    public void createWebResourceSet(ResourceSetType var1, String var2, String var3, String var4, String var5);

    public void addPreResources(WebResourceSet var1);

    public WebResourceSet[] getPreResources();

    public void addJarResources(WebResourceSet var1);

    public WebResourceSet[] getJarResources();

    public void addPostResources(WebResourceSet var1);

    public WebResourceSet[] getPostResources();

    public Context getContext();

    public void setContext(Context var1);

    public void setAllowLinking(boolean var1);

    public boolean getAllowLinking();

    public void setCachingAllowed(boolean var1);

    public boolean isCachingAllowed();

    public void setCacheTtl(long var1);

    public long getCacheTtl();

    public void setCacheMaxSize(long var1);

    public long getCacheMaxSize();

    public void setCacheObjectMaxSize(int var1);

    public int getCacheObjectMaxSize();

    public void setTrackLockedFiles(boolean var1);

    public boolean getTrackLockedFiles();

    public void setArchiveIndexStrategy(String var1);

    public String getArchiveIndexStrategy();

    public ArchiveIndexStrategy getArchiveIndexStrategyEnum();

    public void backgroundProcess();

    public void registerTrackedResource(TrackedWebResource var1);

    public void deregisterTrackedResource(TrackedWebResource var1);

    public List<URL> getBaseUrls();

    public void gc();

    default public CacheStrategy getCacheStrategy() {
        return null;
    }

    default public void setCacheStrategy(CacheStrategy strategy) {
    }

    public static interface CacheStrategy {
        public boolean noCache(String var1);
    }

    public static enum ArchiveIndexStrategy {
        SIMPLE(false, false),
        BLOOM(true, true),
        PURGED(true, false);

        private final boolean usesBloom;
        private final boolean retain;

        private ArchiveIndexStrategy(boolean usesBloom, boolean retain) {
            this.usesBloom = usesBloom;
            this.retain = retain;
        }

        public boolean getUsesBloom() {
            return this.usesBloom;
        }

        public boolean getRetain() {
            return this.retain;
        }
    }

    public static enum ResourceSetType {
        PRE,
        RESOURCE_JAR,
        POST,
        CLASSES_JAR;

    }
}

