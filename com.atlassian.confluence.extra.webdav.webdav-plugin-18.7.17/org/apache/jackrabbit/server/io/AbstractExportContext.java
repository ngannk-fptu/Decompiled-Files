/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import javax.jcr.Item;
import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExportContext
implements ExportContext {
    private static Logger log = LoggerFactory.getLogger(AbstractExportContext.class);
    private final IOListener ioListener;
    private final Item exportRoot;
    private final boolean hasStream;
    protected boolean completed;

    public AbstractExportContext(Item exportRoot, boolean hasStream, IOListener ioListener) {
        this.exportRoot = exportRoot;
        this.hasStream = hasStream;
        this.ioListener = ioListener != null ? ioListener : new DefaultIOListener(log);
    }

    @Override
    public IOListener getIOListener() {
        return this.ioListener;
    }

    @Override
    public Item getExportRoot() {
        return this.exportRoot;
    }

    @Override
    public boolean hasStream() {
        return this.hasStream;
    }

    @Override
    public void informCompleted(boolean success) {
        this.completed = true;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    protected void checkCompleted() {
        if (this.completed) {
            throw new IllegalStateException("ExportContext has already been finalized.");
        }
    }
}

