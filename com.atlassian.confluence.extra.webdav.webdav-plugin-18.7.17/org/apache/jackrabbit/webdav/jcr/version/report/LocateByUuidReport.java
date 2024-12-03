/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatus;
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

public class LocateByUuidReport
extends AbstractJcrReport {
    private static Logger log = LoggerFactory.getLogger(LocateByUuidReport.class);
    public static final ReportType LOCATE_BY_UUID_REPORT = ReportType.register("locate-by-uuid", ItemResourceConstants.NAMESPACE, LocateByUuidReport.class);
    private MultiStatus ms;

    @Override
    public ReportType getType() {
        return LOCATE_BY_UUID_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return true;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        if (!info.containsContentElement("href", DavConstants.NAMESPACE)) {
            throw new DavException(400, "dcr:locate-by-uuid element must at least contain a single DAV:href child.");
        }
        try {
            Element hrefElem = info.getContentElement("href", DavConstants.NAMESPACE);
            String uuid = DomUtil.getTextTrim(hrefElem);
            DavResourceLocator resourceLoc = resource.getLocator();
            Node n = this.getRepositorySession().getNodeByUUID(uuid);
            DavResourceLocator loc = resourceLoc.getFactory().createResourceLocator(resourceLoc.getPrefix(), resourceLoc.getWorkspacePath(), n.getPath(), false);
            DavResource locatedResource = resource.getFactory().createResource(loc, resource.getSession());
            this.ms = new MultiStatus();
            this.ms.addResourceProperties(locatedResource, info.getPropertyNameSet(), info.getDepth());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public Element toXml(Document document) {
        return this.ms.toXml(document);
    }
}

