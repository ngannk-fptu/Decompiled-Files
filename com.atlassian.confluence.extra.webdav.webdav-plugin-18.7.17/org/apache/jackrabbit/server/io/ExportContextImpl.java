/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Item;
import org.apache.jackrabbit.server.io.AbstractExportContext;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportContextImpl
extends AbstractExportContext {
    private static Logger log = LoggerFactory.getLogger(ExportContextImpl.class);
    private final Map<String, String> properties = new HashMap<String, String>();
    private final OutputContext outputCtx;
    private File outFile;
    private OutputStream outStream;

    public ExportContextImpl(Item exportRoot, OutputContext outputCtx) throws IOException {
        super(exportRoot, outputCtx != null && outputCtx.hasStream(), null);
        this.outputCtx = outputCtx;
        if (this.hasStream()) {
            this.outFile = File.createTempFile("__exportcontext", "tmp");
        }
    }

    @Override
    public OutputStream getOutputStream() {
        this.checkCompleted();
        if (this.hasStream()) {
            try {
                if (this.outStream != null) {
                    this.outStream.close();
                }
                this.outStream = new FileOutputStream(this.outFile);
                return this.outStream;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public void setContentLanguage(String contentLanguage) {
        this.properties.put("Content-Language", contentLanguage);
    }

    @Override
    public void setContentLength(long contentLength) {
        this.properties.put("Content-Length", contentLength + "");
    }

    @Override
    public void setContentType(String mimeType, String encoding) {
        this.properties.put("Content-Type", IOUtil.buildContentType(mimeType, encoding));
    }

    @Override
    public void setCreationTime(long creationTime) {
    }

    @Override
    public void setModificationTime(long modificationTime) {
        if (modificationTime <= -1L) {
            modificationTime = new Date().getTime();
        }
        String lastMod = IOUtil.getLastModified(modificationTime);
        this.properties.put("Last-Modified", lastMod);
    }

    @Override
    public void setETag(String etag) {
        this.properties.put("ETag", etag);
    }

    @Override
    public void setProperty(Object propertyName, Object propertyValue) {
        if (propertyName != null && propertyValue != null) {
            this.properties.put(propertyName.toString(), propertyValue.toString());
        }
    }

    @Override
    public void informCompleted(boolean success) {
        this.checkCompleted();
        this.completed = true;
        if (this.outStream != null) {
            try {
                this.outStream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (success && this.outputCtx != null) {
            boolean seenContentLength = false;
            for (Map.Entry<String, String> entry : this.properties.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (name == null || value == null) continue;
                this.outputCtx.setProperty(name, value);
                seenContentLength |= "Content-Length".equals(name);
            }
            if (this.outputCtx.hasStream() && this.outFile != null) {
                OutputStream out = this.outputCtx.getOutputStream();
                try {
                    if (!seenContentLength) {
                        this.outputCtx.setContentLength(this.outFile.length());
                    }
                    FileInputStream in = new FileInputStream(this.outFile);
                    IOUtil.spool(in, out);
                }
                catch (IOException e) {
                    log.error(e.toString());
                }
            }
        }
        if (this.outFile != null) {
            this.outFile.delete();
        }
    }
}

