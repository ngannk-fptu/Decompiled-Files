/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.AbstractExportContext;
import org.apache.jackrabbit.server.io.BoundedInputStream;
import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.ImportContextImpl;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipHandler
extends DefaultHandler {
    private static Logger log = LoggerFactory.getLogger(ZipHandler.class);
    public static final String ZIP_MIMETYPE = "application/zip";
    private boolean intermediateSave;

    public ZipHandler() {
    }

    public ZipHandler(IOManager ioManager) {
        this(ioManager, "nt:folder", "nt:file", "nt:unstructured");
    }

    public ZipHandler(IOManager ioManager, String collectionNodetype, String defaultNodetype, String contentNodetype) {
        super(ioManager, collectionNodetype, defaultNodetype, contentNodetype);
        if (ioManager == null) {
            throw new IllegalArgumentException("The IOManager must not be null.");
        }
    }

    public void setIntermediateSave(boolean intermediateSave) {
        this.intermediateSave = intermediateSave;
    }

    @Override
    public boolean canImport(ImportContext context, boolean isCollection) {
        if (context == null || context.isCompleted()) {
            return false;
        }
        boolean isZip = ZIP_MIMETYPE.equals(context.getMimeType());
        return isZip && context.hasStream() && super.canImport(context, isCollection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean importData(ImportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        boolean success = true;
        InputStream in = context.getInputStream();
        ZipInputStream zin = new ZipInputStream(in);
        try {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null && success) {
                success = this.importZipEntry(zin, entry, context, contentNode);
                zin.closeEntry();
            }
        }
        finally {
            zin.close();
            in.close();
        }
        return success;
    }

    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
        if (super.canExport(context, isCollection)) {
            String mimeType = null;
            boolean hasDataProperty = false;
            try {
                Node contentNode = this.getContentNode(context, isCollection);
                hasDataProperty = contentNode.hasProperty("jcr:data");
                mimeType = contentNode.hasProperty("jcr:mimeType") ? contentNode.getProperty("jcr:mimeType").getString() : this.detect(context.getExportRoot().getName());
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
            return ZIP_MIMETYPE.equals(mimeType) && !hasDataProperty;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void exportData(ExportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        ZipOutputStream zout = new ZipOutputStream(context.getOutputStream());
        zout.setMethod(8);
        try {
            int pos = contentNode.getPath().length();
            this.exportZipEntry(context, zout, contentNode, pos > 1 ? pos + 1 : pos);
        }
        finally {
            zout.finish();
        }
    }

    private void exportZipEntry(ExportContext context, ZipOutputStream zout, Node node, int pos) throws IOException {
        try {
            if (node.isNodeType(this.getNodeType())) {
                ZipEntryExportContext subctx = new ZipEntryExportContext(node, zout, context, pos);
                zout.putNextEntry(subctx.entry);
                this.getIOManager().exportContent((ExportContext)subctx, false);
            } else {
                NodeIterator niter = node.getNodes();
                while (niter.hasNext()) {
                    this.exportZipEntry(context, zout, niter.nextNode(), pos);
                }
            }
        }
        catch (RepositoryException e) {
            log.error(e.getMessage());
        }
    }

    private boolean importZipEntry(ZipInputStream zin, ZipEntry entry, ImportContext context, Node node) throws RepositoryException, IOException {
        boolean success = false;
        log.debug("entry: " + entry.getName() + " size: " + entry.getSize());
        if (entry.isDirectory()) {
            IOUtil.mkDirs(node, ZipHandler.makeValidJCRPath(entry.getName(), false), this.getCollectionNodeType());
            success = true;
        } else {
            BoundedInputStream bin = new BoundedInputStream(zin);
            bin.setPropagateClose(false);
            ZipEntryImportContext entryContext = new ZipEntryImportContext(context, entry, bin, node);
            IOManager ioManager = this.getIOManager();
            boolean bl = success = ioManager != null ? ioManager.importContent((ImportContext)entryContext, false) : false;
            if (this.intermediateSave) {
                context.getImportRoot().save();
            }
        }
        return success;
    }

    private static String makeValidJCRPath(String label, boolean appendLeadingSlash) {
        if (appendLeadingSlash && !label.startsWith("/")) {
            label = "/" + label;
        }
        StringBuffer ret = new StringBuffer(label.length());
        for (int i = 0; i < label.length(); ++i) {
            int c = label.charAt(i);
            if (c == 42 || c == 39 || c == 34) {
                c = 95;
            } else if (c == 91) {
                c = 40;
            } else if (c == 93) {
                c = 41;
            }
            ret.append((char)c);
        }
        return ret.toString();
    }

    private static class ZipEntryExportContext
    extends AbstractExportContext {
        private ZipEntry entry;
        private OutputStream out;

        private ZipEntryExportContext(Item exportRoot, OutputStream out, ExportContext context, int pos) {
            super(exportRoot, out != null, context.getIOListener());
            this.out = out;
            try {
                String entryPath = exportRoot.getPath().length() > pos ? exportRoot.getPath().substring(pos) : "";
                this.entry = new ZipEntry(entryPath);
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
        }

        @Override
        public OutputStream getOutputStream() {
            return this.out;
        }

        @Override
        public void setContentType(String mimeType, String encoding) {
            if (this.entry != null) {
                this.entry.setComment(mimeType);
            }
        }

        @Override
        public void setContentLanguage(String contentLanguage) {
        }

        @Override
        public void setContentLength(long contentLength) {
            if (this.entry != null) {
                this.entry.setSize(contentLength);
            }
        }

        @Override
        public void setCreationTime(long creationTime) {
        }

        @Override
        public void setModificationTime(long modificationTime) {
            if (this.entry != null) {
                this.entry.setTime(modificationTime);
            }
        }

        @Override
        public void setETag(String etag) {
        }

        @Override
        public void setProperty(Object propertyName, Object propertyValue) {
        }
    }

    private class ZipEntryImportContext
    extends ImportContextImpl {
        private final Item importRoot;
        private final ZipEntry entry;

        private ZipEntryImportContext(ImportContext context, ZipEntry entry, BoundedInputStream bin, Node contentNode) throws IOException, RepositoryException {
            super(contentNode, Text.getName(ZipHandler.makeValidJCRPath(entry.getName(), true)), null, bin, context.getIOListener(), ZipHandler.this.getIOManager().getDetector());
            this.entry = entry;
            String path = ZipHandler.makeValidJCRPath(entry.getName(), true);
            this.importRoot = IOUtil.mkDirs(contentNode, Text.getRelativeParent(path, 1), ZipHandler.this.getCollectionNodeType());
        }

        @Override
        public Item getImportRoot() {
            return this.importRoot;
        }

        @Override
        public long getModificationTime() {
            return this.entry.getTime();
        }

        @Override
        public long getContentLength() {
            return this.entry.getSize();
        }
    }
}

