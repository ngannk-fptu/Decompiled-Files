/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments.lifecycle.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.DataHandlerExtImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileAccessor
extends Observable {
    private static final Log log = LogFactory.getLog(FileAccessor.class);
    File file = null;
    LifecycleManager manager;
    private int accessCount = 0;

    public FileAccessor(LifecycleManager manager, File file) {
        this.manager = manager;
        this.file = file;
    }

    public DataHandler getDataHandler(String contentType) throws MessagingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"getDataHandler()");
            log.debug((Object)("accessCount =" + this.accessCount));
        }
        CachedFileDataSource dataSource = new CachedFileDataSource(this.file);
        dataSource.setContentType(contentType);
        ++this.accessCount;
        this.setChanged();
        this.notifyObservers();
        DataHandler dataHandler = new DataHandler((DataSource)dataSource);
        return new DataHandlerExtImpl(dataHandler, this.manager);
    }

    public String getFileName() throws MessagingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"getFileName()");
        }
        return this.file.getAbsolutePath();
    }

    public InputStream getInputStream() throws IOException, MessagingException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"getInputStream()");
        }
        return new FileInputStream(this.file);
    }

    public OutputStream getOutputStream() throws FileNotFoundException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"getOutputStream()");
        }
        return new FileOutputStream(this.file);
    }

    public long getSize() {
        return this.file.length();
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getAccessCount() {
        return this.accessCount;
    }
}

