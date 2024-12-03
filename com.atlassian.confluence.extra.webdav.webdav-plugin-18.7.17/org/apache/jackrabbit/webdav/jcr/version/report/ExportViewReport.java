/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExportViewReport
extends AbstractJcrReport {
    private static Logger log = LoggerFactory.getLogger(ExportViewReport.class);
    public static final ReportType EXPORTVIEW_REPORT = ReportType.register("exportview", ItemResourceConstants.NAMESPACE, ExportViewReport.class);
    private String absNodePath;

    @Override
    public ReportType getType() {
        return EXPORTVIEW_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        this.absNodePath = resource.getLocator().getRepositoryPath();
        try {
            if (!this.getRepositorySession().itemExists(this.absNodePath) || !this.getRepositorySession().getItem(this.absNodePath).isNode()) {
                throw new JcrDavException(new PathNotFoundException(this.absNodePath + " does not exist."));
            }
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public Element toXml(Document document) {
        boolean skipBinary = this.getReportInfo().containsContentElement("skipbinary", ItemResourceConstants.NAMESPACE);
        boolean noRecurse = this.getReportInfo().containsContentElement("norecurse", ItemResourceConstants.NAMESPACE);
        try {
            String prefix = "_tmp_" + Text.getName(this.absNodePath);
            File tmpfile = File.createTempFile(prefix, null, null);
            tmpfile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tmpfile);
            if (this.getReportInfo().containsContentElement("sysview", ItemResourceConstants.NAMESPACE)) {
                this.getRepositorySession().exportSystemView(this.absNodePath, out, skipBinary, noRecurse);
            } else {
                this.getRepositorySession().exportDocumentView(this.absNodePath, out, skipBinary, noRecurse);
            }
            out.close();
            Document tmpDoc = DomUtil.parseDocument(new FileInputStream(tmpfile));
            Element rootElem = (Element)document.importNode(tmpDoc.getDocumentElement(), true);
            return rootElem;
        }
        catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        catch (ParserConfigurationException e) {
            log.error(e.getMessage());
        }
        catch (SAXException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

