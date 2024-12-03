/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.jcr.Item;
import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportContextImpl
implements ImportContext {
    private static Logger log = LoggerFactory.getLogger(ImportContextImpl.class);
    private final IOListener ioListener;
    private final Item importRoot;
    private final String systemId;
    private final File inputFile;
    private InputContext inputCtx;
    private boolean completed;
    private final MediaType type;

    public ImportContextImpl(Item importRoot, String systemId, InputContext inputCtx, InputStream stream, IOListener ioListener, Detector detector) throws IOException {
        this.importRoot = importRoot;
        this.systemId = systemId;
        this.inputCtx = inputCtx;
        this.ioListener = ioListener != null ? ioListener : new DefaultIOListener(log);
        Metadata metadata = new Metadata();
        if (inputCtx != null && inputCtx.getContentType() != null) {
            metadata.set("Content-Type", inputCtx.getContentType());
        }
        if (systemId != null) {
            metadata.set("resourceName", systemId);
        }
        if (stream != null && !stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        this.type = detector.detect(stream, metadata);
        this.inputFile = IOUtil.getTempFile(stream);
    }

    @Override
    public IOListener getIOListener() {
        return this.ioListener;
    }

    @Override
    public Item getImportRoot() {
        return this.importRoot;
    }

    @Override
    public boolean hasStream() {
        return this.inputFile != null;
    }

    @Override
    public InputStream getInputStream() {
        this.checkCompleted();
        FileInputStream in = null;
        if (this.inputFile != null) {
            try {
                in = new FileInputStream(this.inputFile);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return in;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public long getModificationTime() {
        return this.inputCtx != null ? this.inputCtx.getModificationTime() : new Date().getTime();
    }

    @Override
    public String getContentLanguage() {
        return this.inputCtx != null ? this.inputCtx.getContentLanguage() : null;
    }

    @Override
    public long getContentLength() {
        long length = -1L;
        if (this.inputCtx != null) {
            length = this.inputCtx.getContentLength();
        }
        if (length < 0L && this.inputFile != null) {
            length = this.inputFile.length();
        }
        if (length < 0L) {
            log.debug("Unable to determine content length -> default value = -1");
        }
        return length;
    }

    @Override
    public String getMimeType() {
        return IOUtil.getMimeType(this.type.toString());
    }

    @Override
    public String getEncoding() {
        return IOUtil.getEncoding(this.type.toString());
    }

    @Override
    public Object getProperty(Object propertyName) {
        return this.inputCtx != null ? this.inputCtx.getProperty(propertyName.toString()) : null;
    }

    @Override
    public void informCompleted(boolean success) {
        this.checkCompleted();
        this.completed = true;
        if (this.inputFile != null) {
            this.inputFile.delete();
        }
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    private void checkCompleted() {
        if (this.completed) {
            throw new IllegalStateException("ImportContext has already been consumed.");
        }
    }
}

