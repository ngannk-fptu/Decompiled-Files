/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.PropEntry;

public class VersionHistoryHandler
implements IOHandler,
PropertyHandler {
    private IOManager ioManager;

    public VersionHistoryHandler() {
    }

    public VersionHistoryHandler(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override
    public IOManager getIOManager() {
        return this.ioManager;
    }

    @Override
    public void setIOManager(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean canImport(ImportContext context, boolean isCollection) {
        return false;
    }

    @Override
    public boolean canImport(ImportContext context, DavResource resource) {
        return false;
    }

    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
        if (context == null) {
            return false;
        }
        return context.getExportRoot() instanceof VersionHistory;
    }

    @Override
    public boolean canExport(ExportContext context, DavResource resource) {
        if (context == null) {
            return false;
        }
        return context.getExportRoot() instanceof VersionHistory;
    }

    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        Item exportRoot = context.getExportRoot();
        if (exportRoot instanceof VersionHistory) {
            return this.export(context);
        }
        return false;
    }

    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        Item exportRoot = context.getExportRoot();
        if (exportRoot instanceof VersionHistory) {
            return this.export(context);
        }
        return false;
    }

    @Override
    public boolean canImport(PropertyImportContext context, boolean isCollection) {
        return false;
    }

    @Override
    public Map<? extends PropEntry, ?> importProperties(PropertyImportContext importContext, boolean isCollection) throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canExport(PropertyExportContext context, boolean isCollection) {
        return this.canExport((ExportContext)context, isCollection);
    }

    @Override
    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws RepositoryException {
        if (!this.canExport(exportContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + this.getName() + " failed to export properties.");
        }
        return this.export(exportContext);
    }

    private boolean export(ExportContext exportContext) {
        exportContext.setContentLength(0L);
        exportContext.setModificationTime(-1L);
        return true;
    }
}

