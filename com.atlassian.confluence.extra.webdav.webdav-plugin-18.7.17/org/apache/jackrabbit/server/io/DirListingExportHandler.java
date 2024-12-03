/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirListingExportHandler
implements IOHandler,
PropertyHandler {
    private static Logger log = LoggerFactory.getLogger(DirListingExportHandler.class);
    private IOManager ioManager;

    public DirListingExportHandler() {
    }

    public DirListingExportHandler(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override
    public boolean canImport(ImportContext context, boolean isFolder) {
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
        if (context == null || context.isCompleted()) {
            return false;
        }
        return isCollection && context.getExportRoot() != null;
    }

    @Override
    public boolean canExport(ExportContext context, DavResource resource) {
        if (resource == null) {
            return false;
        }
        return this.canExport(context, resource.isCollection());
    }

    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        if (!this.canExport(context, isCollection)) {
            throw new IOException(this.getName() + ": Cannot export " + context.getExportRoot());
        }
        context.setModificationTime(new Date().getTime());
        context.setContentType("text/html", "UTF-8");
        if (context.hasStream()) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(context.getOutputStream(), "utf8"));
            try {
                Item item = context.getExportRoot();
                Repository rep = item.getSession().getRepository();
                String repName = rep.getDescriptor("jcr.repository.name");
                String repURL = rep.getDescriptor("jcr.repository.vendor.url");
                String repVersion = rep.getDescriptor("jcr.repository.version");
                writer.print("<html><head><title>");
                writer.print(Text.encodeIllegalHTMLCharacters(repName));
                writer.print(" ");
                writer.print(Text.encodeIllegalHTMLCharacters(repVersion));
                writer.print(" ");
                writer.print(Text.encodeIllegalHTMLCharacters(item.getPath()));
                writer.print("</title></head>");
                writer.print("<body><h2>");
                writer.print(Text.encodeIllegalHTMLCharacters(item.getPath()));
                writer.print("</h2><ul>");
                writer.print("<li><a href=\"..\">..</a></li>");
                if (item.isNode()) {
                    NodeIterator iter = ((Node)item).getNodes();
                    while (iter.hasNext()) {
                        Node child = iter.nextNode();
                        String label = Text.getName(child.getPath());
                        writer.print("<li><a href=\"");
                        writer.print(Text.encodeIllegalHTMLCharacters(Text.escape(label)));
                        if (child.isNode()) {
                            writer.print("/");
                        }
                        writer.print("\">");
                        writer.print(Text.encodeIllegalHTMLCharacters(label));
                        writer.print("</a></li>");
                    }
                }
                writer.print("</ul><hr size=\"1\"><em>Powered by <a href=\"");
                writer.print(Text.encodeIllegalHTMLCharacters(repURL));
                writer.print("\">");
                writer.print(Text.encodeIllegalHTMLCharacters(repName));
                writer.print("</a> version ");
                writer.print(Text.encodeIllegalHTMLCharacters(repVersion));
                writer.print("</em></body></html>");
            }
            catch (RepositoryException e) {
                log.debug(e.getMessage());
            }
            writer.close();
        }
        return true;
    }

    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        if (!this.canExport(context, resource)) {
            throw new IOException(this.getName() + ": Cannot export " + context.getExportRoot());
        }
        context.setModificationTime(new Date().getTime());
        context.setContentType("text/html", "UTF-8");
        if (context.hasStream()) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(context.getOutputStream(), "utf8"));
            try {
                Item item = context.getExportRoot();
                Repository rep = item.getSession().getRepository();
                String repName = rep.getDescriptor("jcr.repository.name");
                String repURL = rep.getDescriptor("jcr.repository.vendor.url");
                String repVersion = rep.getDescriptor("jcr.repository.version");
                writer.print("<html><head><title>");
                writer.print(Text.encodeIllegalHTMLCharacters(repName));
                writer.print(" ");
                writer.print(Text.encodeIllegalHTMLCharacters(repVersion));
                writer.print(" ");
                writer.print(Text.encodeIllegalHTMLCharacters(resource.getResourcePath()));
                writer.print("</title></head>");
                writer.print("<body><h2>");
                writer.print(Text.encodeIllegalHTMLCharacters(resource.getResourcePath()));
                writer.print("</h2><ul>");
                writer.print("<li><a href=\"..\">..</a></li>");
                DavResourceIterator iter = resource.getMembers();
                while (iter.hasNext()) {
                    DavResource child = iter.nextResource();
                    String label = Text.getName(child.getResourcePath());
                    writer.print("<li><a href=\"");
                    writer.print(Text.encodeIllegalHTMLCharacters(child.getHref()));
                    writer.print("\">");
                    writer.print(Text.encodeIllegalHTMLCharacters(label));
                    writer.print("</a></li>");
                }
                writer.print("</ul><hr size=\"1\"><em>Powered by <a href=\"");
                writer.print(Text.encodeIllegalHTMLCharacters(repURL));
                writer.print("\">");
                writer.print(Text.encodeIllegalHTMLCharacters(repName));
                writer.print("</a> version ");
                writer.print(Text.encodeIllegalHTMLCharacters(repVersion));
                writer.print("</em></body></html>");
            }
            catch (RepositoryException e) {
                log.debug(e.getMessage());
            }
            writer.close();
        }
        return true;
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
        return "DirListing Export";
    }

    @Override
    public boolean canExport(PropertyExportContext context, boolean isCollection) {
        return false;
    }

    @Override
    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws RepositoryException {
        throw new RepositoryException(this.getName() + ": Cannot export properties for context " + exportContext);
    }

    @Override
    public boolean canImport(PropertyImportContext context, boolean isCollection) {
        return false;
    }

    @Override
    public Map<? extends PropEntry, ?> importProperties(PropertyImportContext importContext, boolean isCollection) throws RepositoryException {
        throw new RepositoryException(this.getName() + ": Cannot import properties.");
    }
}

