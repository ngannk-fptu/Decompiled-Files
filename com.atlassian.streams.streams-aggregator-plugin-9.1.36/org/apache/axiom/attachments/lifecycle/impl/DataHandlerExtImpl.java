/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.attachments.lifecycle.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataHandlerExtImpl
extends DataHandler
implements DataHandlerExt,
Observer {
    private static final Log log = LogFactory.getLog(DataHandlerExtImpl.class);
    private DataHandler dataHandler = null;
    private LifecycleManager manager = null;
    private static int READ_COUNT = 1;
    private boolean deleteOnreadOnce = false;

    public DataHandlerExtImpl(DataHandler dataHandler, LifecycleManager manager) {
        super(dataHandler.getDataSource());
        this.dataHandler = dataHandler;
        this.manager = manager;
    }

    public InputStream readOnce() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void deleteWhenReadOnce() throws IOException {
        this.deleteOnreadOnce = true;
        FileAccessor fa = this.manager.getFileAccessor(this.getName());
        if (fa == null) {
            log.warn((Object)"Could not find FileAccessor, delete on readOnce Failed");
            return;
        }
        if (fa.getAccessCount() >= READ_COUNT) {
            this.purgeDataSource();
        } else {
            fa.addObserver(this);
        }
    }

    public void purgeDataSource() throws IOException {
        File file;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Start purgeDataSource");
        }
        if ((file = this.getFile()) != null) {
            this.manager.delete(file);
        } else if (log.isDebugEnabled()) {
            log.debug((Object)"DataSource is not a CachedFileDataSource, Unable to Purge.");
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"End purgeDataSource");
        }
    }

    public void update(Observable o, Object arg) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Start update in Observer");
            }
            if (o instanceof FileAccessor) {
                FileAccessor fa = (FileAccessor)o;
                if (this.deleteOnreadOnce && fa.getAccessCount() >= READ_COUNT) {
                    this.purgeDataSource();
                }
            }
        }
        catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"delete on readOnce Failed");
            }
            log.warn((Object)("delete on readOnce Failed with IOException in Observer" + e.getMessage()));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"End update in Observer");
        }
    }

    private File getFile() {
        DataSource dataSource = this.dataHandler.getDataSource();
        if (dataSource instanceof CachedFileDataSource) {
            CachedFileDataSource cds = (CachedFileDataSource)dataSource;
            return cds.getFile();
        }
        return null;
    }
}

