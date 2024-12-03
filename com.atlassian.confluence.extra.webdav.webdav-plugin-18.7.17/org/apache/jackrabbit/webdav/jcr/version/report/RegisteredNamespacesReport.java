/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RegisteredNamespacesReport
extends AbstractJcrReport
implements ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(RegisteredNamespacesReport.class);
    public static final ReportType REGISTERED_NAMESPACES_REPORT = ReportType.register("registerednamespaces", ItemResourceConstants.NAMESPACE, RegisteredNamespacesReport.class);
    private NamespaceRegistry nsReg;

    @Override
    public ReportType getType() {
        return REGISTERED_NAMESPACES_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        try {
            this.nsReg = this.getRepositorySession().getWorkspace().getNamespaceRegistry();
        }
        catch (RepositoryException e) {
            throw new DavException(500);
        }
    }

    @Override
    public Element toXml(Document document) {
        Element report = DomUtil.createElement(document, "registerednamespaces-report", NAMESPACE);
        try {
            for (String prefix : this.nsReg.getPrefixes()) {
                Element elem = DomUtil.addChildElement(report, "namespace", NAMESPACE);
                DomUtil.addChildElement(elem, "prefix", NAMESPACE, prefix);
                DomUtil.addChildElement(elem, "uri", NAMESPACE, this.nsReg.getURI(prefix));
            }
        }
        catch (RepositoryException e) {
            log.error(e.getMessage());
        }
        return report;
    }
}

