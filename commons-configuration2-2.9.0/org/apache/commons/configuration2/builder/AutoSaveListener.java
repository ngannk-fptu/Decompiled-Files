/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileHandlerListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class AutoSaveListener
extends FileHandlerListenerAdapter
implements EventListener<ConfigurationEvent> {
    private final Log log = LogFactory.getLog(this.getClass());
    private final FileBasedConfigurationBuilder<?> builder;
    private FileHandler handler;
    private int loading;

    public AutoSaveListener(FileBasedConfigurationBuilder<?> bldr) {
        this.builder = bldr;
    }

    @Override
    public void onEvent(ConfigurationEvent event) {
        if (this.autoSaveRequired(event)) {
            try {
                this.builder.save();
            }
            catch (ConfigurationException ce) {
                this.log.warn((Object)"Auto save failed!", (Throwable)ce);
            }
        }
    }

    @Override
    public synchronized void loading(FileHandler handler) {
        ++this.loading;
    }

    @Override
    public synchronized void loaded(FileHandler handler) {
        --this.loading;
    }

    public synchronized void updateFileHandler(FileHandler fh) {
        if (this.handler != null) {
            this.handler.removeFileHandlerListener(this);
        }
        if (fh != null) {
            fh.addFileHandlerListener(this);
        }
        this.handler = fh;
    }

    private synchronized boolean inLoadOperation() {
        return this.loading > 0;
    }

    private boolean autoSaveRequired(ConfigurationEvent event) {
        return !event.isBeforeUpdate() && !this.inLoadOperation();
    }
}

