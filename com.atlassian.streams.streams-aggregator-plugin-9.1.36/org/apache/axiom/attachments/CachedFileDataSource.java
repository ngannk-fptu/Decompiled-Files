/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.FileDataSource
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments;

import java.io.File;
import java.io.IOException;
import javax.activation.FileDataSource;
import org.apache.axiom.attachments.AttachmentCacheMonitor;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachedFileDataSource
extends FileDataSource
implements SizeAwareDataSource {
    private static final Log log = LogFactory.getLog(CachedFileDataSource.class);
    String contentType = null;
    private static AttachmentCacheMonitor acm = AttachmentCacheMonitor.getAttachmentCacheMonitor();
    private String cachedFileName = null;

    public CachedFileDataSource(File file) {
        super(file);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter CachedFileDataSource ctor");
        }
        if (file != null) {
            try {
                this.cachedFileName = file.getCanonicalPath();
            }
            catch (IOException e) {
                log.error((Object)("IOException caught: " + e));
            }
        }
        if (this.cachedFileName != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Cached file: " + this.cachedFileName));
                log.debug((Object)"Registering the file with AttachmentCacheMonitor and also marked it as being accessed");
            }
            acm.access(this.cachedFileName);
            acm.register(this.cachedFileName);
        }
    }

    public String getContentType() {
        if (this.contentType != null) {
            return this.contentType;
        }
        return super.getContentType();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return this.getFile().length();
    }
}

