/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.io.InputStream;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.TrackedInputStream;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractResource
implements WebResource {
    protected static final StringManager sm = StringManager.getManager(AbstractResource.class);
    private final WebResourceRoot root;
    private final String webAppPath;
    private String mimeType = null;
    private volatile String weakETag;

    protected AbstractResource(WebResourceRoot root, String webAppPath) {
        this.root = root;
        this.webAppPath = webAppPath;
    }

    @Override
    public final WebResourceRoot getWebResourceRoot() {
        return this.root;
    }

    @Override
    public final String getWebappPath() {
        return this.webAppPath;
    }

    @Override
    public final String getLastModifiedHttp() {
        return FastHttpDateFormat.formatDate((long)this.getLastModified());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final String getETag() {
        if (this.weakETag == null) {
            AbstractResource abstractResource = this;
            synchronized (abstractResource) {
                if (this.weakETag == null) {
                    long contentLength = this.getContentLength();
                    long lastModified = this.getLastModified();
                    if (contentLength >= 0L || lastModified >= 0L) {
                        this.weakETag = "W/\"" + contentLength + "-" + lastModified + "\"";
                    }
                }
            }
        }
        return this.weakETag;
    }

    @Override
    public final void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public final String getMimeType() {
        return this.mimeType;
    }

    @Override
    public final InputStream getInputStream() {
        InputStream is = this.doGetInputStream();
        if (is == null || !this.root.getTrackLockedFiles()) {
            return is;
        }
        return new TrackedInputStream(this.root, this.getName(), is);
    }

    protected abstract InputStream doGetInputStream();

    protected abstract Log getLog();
}

