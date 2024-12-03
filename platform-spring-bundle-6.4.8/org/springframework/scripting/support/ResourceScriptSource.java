/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.scripting.support;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

public class ResourceScriptSource
implements ScriptSource {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private EncodedResource resource;
    private long lastModified = -1L;
    private final Object lastModifiedMonitor = new Object();

    public ResourceScriptSource(EncodedResource resource) {
        Assert.notNull((Object)resource, "Resource must not be null");
        this.resource = resource;
    }

    public ResourceScriptSource(Resource resource) {
        Assert.notNull((Object)resource, "Resource must not be null");
        this.resource = new EncodedResource(resource, "UTF-8");
    }

    public final Resource getResource() {
        return this.resource.getResource();
    }

    public void setEncoding(@Nullable String encoding) {
        this.resource = new EncodedResource(this.resource.getResource(), encoding);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getScriptAsString() throws IOException {
        Object object = this.lastModifiedMonitor;
        synchronized (object) {
            this.lastModified = this.retrieveLastModifiedTime();
        }
        Reader reader = this.resource.getReader();
        return FileCopyUtils.copyToString(reader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isModified() {
        Object object = this.lastModifiedMonitor;
        synchronized (object) {
            return this.lastModified < 0L || this.retrieveLastModifiedTime() > this.lastModified;
        }
    }

    protected long retrieveLastModifiedTime() {
        try {
            return this.getResource().lastModified();
        }
        catch (IOException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)(this.getResource() + " could not be resolved in the file system - current timestamp not available for script modification check"), (Throwable)ex);
            }
            return 0L;
        }
    }

    @Override
    @Nullable
    public String suggestedClassName() {
        String filename = this.getResource().getFilename();
        return filename != null ? StringUtils.stripFilenameExtension(filename) : null;
    }

    public String toString() {
        return this.resource.toString();
    }
}

