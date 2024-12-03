/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.tika.detect.Detector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOManagerImpl
implements IOManager {
    private static Logger log = LoggerFactory.getLogger(IOManagerImpl.class);
    private Detector detector;
    private final List<IOHandler> ioHandlers = new ArrayList<IOHandler>();

    @Override
    public void addIOHandler(IOHandler ioHandler) {
        if (ioHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid IOHandler.");
        }
        ioHandler.setIOManager(this);
        this.ioHandlers.add(ioHandler);
    }

    @Override
    public IOHandler[] getIOHandlers() {
        return this.ioHandlers.toArray(new IOHandler[this.ioHandlers.size()]);
    }

    @Override
    public Detector getDetector() {
        return this.detector;
    }

    @Override
    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        boolean success = false;
        if (context != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = this.getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; ++i) {
                IOHandler ioh = ioHandlers[i];
                if (!ioh.canImport(context, isCollection)) continue;
                ioListener.onBegin(ioh, context);
                success = ioh.importContent(context, isCollection);
                ioListener.onEnd(ioh, context, success);
            }
            context.informCompleted(success);
        }
        return success;
    }

    @Override
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        boolean success = false;
        if (context != null && resource != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = this.getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; ++i) {
                IOHandler ioh = ioHandlers[i];
                if (!ioh.canImport(context, resource)) continue;
                ioListener.onBegin(ioh, context);
                success = ioh.importContent(context, resource);
                ioListener.onEnd(ioh, context, success);
            }
            context.informCompleted(success);
        }
        return success;
    }

    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        boolean success = false;
        if (context != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = this.getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; ++i) {
                IOHandler ioh = ioHandlers[i];
                if (!ioh.canExport(context, isCollection)) continue;
                ioListener.onBegin(ioh, context);
                success = ioh.exportContent(context, isCollection);
                ioListener.onEnd(ioh, context, success);
            }
            context.informCompleted(success);
        }
        return success;
    }

    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        boolean success = false;
        if (context != null && resource != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = this.getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; ++i) {
                IOHandler ioh = ioHandlers[i];
                if (!ioh.canExport(context, resource)) continue;
                ioListener.onBegin(ioh, context);
                success = ioh.exportContent(context, resource);
                ioListener.onEnd(ioh, context, success);
            }
            context.informCompleted(success);
        }
        return success;
    }
}

