/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.PropEntry;

public class VersionHandler
extends DefaultHandler
implements IOHandler {
    public VersionHandler() {
    }

    public VersionHandler(IOManager ioManager) {
        super(ioManager);
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
        return false;
    }

    @Override
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        return false;
    }

    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
        if (context == null) {
            return false;
        }
        return context.getExportRoot() instanceof Version;
    }

    @Override
    public boolean canExport(ExportContext context, DavResource resource) {
        if (context == null) {
            return false;
        }
        return context.getExportRoot() instanceof Version;
    }

    @Override
    public boolean canImport(PropertyImportContext context, boolean isCollection) {
        return false;
    }

    @Override
    public Map<? extends PropEntry, ?> importProperties(PropertyImportContext importContext, boolean isCollection) throws RepositoryException {
        throw new RepositoryException("Properties cannot be imported");
    }

    @Override
    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws RepositoryException {
        if (!this.canExport(exportContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + this.getName() + " failed to export properties.");
        }
        Node cn = this.getContentNode(exportContext, isCollection);
        try {
            this.exportProperties(exportContext, isCollection, cn);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    protected Node getContentNode(ExportContext context, boolean isCollection) throws RepositoryException {
        Node node = (Node)context.getExportRoot();
        Node frozenNode = node.getNode("jcr:frozenNode");
        if (frozenNode.hasNode("jcr:content")) {
            return frozenNode.getNode("jcr:content");
        }
        return frozenNode;
    }
}

