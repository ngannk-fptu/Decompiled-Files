/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;

public class XmlHandler
extends DefaultHandler {
    public static final String XML_MIMETYPE = "text/xml";
    public static final String XML_MIMETYPE_ALT = "application/xml";
    private static final Set<String> supportedTypes = new HashSet<String>();

    public XmlHandler() {
    }

    public XmlHandler(IOManager ioManager) {
        super(ioManager, "nt:unstructured", "nt:file", "nt:unstructured");
    }

    public XmlHandler(IOManager ioManager, String collectionNodetype, String defaultNodetype, String contentNodetype) {
        super(ioManager, collectionNodetype, defaultNodetype, contentNodetype);
    }

    @Override
    public boolean canImport(ImportContext context, boolean isCollection) {
        return context != null && !context.isCompleted() && supportedTypes.contains(context.getMimeType()) && context.hasStream() && context.getContentLength() > 0L && super.canImport(context, isCollection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean importData(ImportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        int uuidBehavior = isCollection ? 1 : 0;
        try (InputStream in = context.getInputStream();){
            contentNode.getSession().importXML(contentNode.getPath(), in, uuidBehavior);
        }
        return true;
    }

    @Override
    protected boolean importProperties(ImportContext context, boolean isCollection, Node contentNode) {
        boolean success = super.importProperties(context, isCollection, contentNode);
        if (success) {
            try {
                contentNode.setProperty("jcr:encoding", "UTF-8");
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
        }
        return success;
    }

    @Override
    protected boolean forceCompatibleContentNodes() {
        return true;
    }

    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
        if (super.canExport(context, isCollection)) {
            String mimeType = null;
            try {
                Node contentNode = this.getContentNode(context, isCollection);
                mimeType = contentNode.hasProperty("jcr:mimeType") ? contentNode.getProperty("jcr:mimeType").getString() : this.detect(context.getExportRoot().getName());
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
            return XML_MIMETYPE.equals(mimeType);
        }
        return false;
    }

    @Override
    protected void exportData(ExportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        if (contentNode.getNodes().hasNext()) {
            contentNode = contentNode.getNodes().nextNode();
        }
        OutputStream out = context.getOutputStream();
        contentNode.getSession().exportDocumentView(contentNode.getPath(), out, true, false);
    }

    @Override
    protected void exportProperties(ExportContext context, boolean isCollection, Node contentNode) throws IOException {
        super.exportProperties(context, isCollection, contentNode);
        try {
            if (!contentNode.hasProperty("jcr:mimeType")) {
                context.setContentType(XML_MIMETYPE, "UTF-8");
            }
        }
        catch (RepositoryException e) {
            throw new IOException(e.getMessage());
        }
    }

    static {
        supportedTypes.add(XML_MIMETYPE);
        supportedTypes.add(XML_MIMETYPE_ALT);
    }
}

