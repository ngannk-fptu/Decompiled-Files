/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.jcr.version.report.LocateByUuidReport;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LocateCorrespondingNodeReport
extends AbstractJcrReport {
    private static Logger log = LoggerFactory.getLogger(LocateCorrespondingNodeReport.class);
    private String correspHref;
    public static final ReportType LOCATE_CORRESPONDING_NODE_REPORT = ReportType.register("locate-corresponding-node", ItemResourceConstants.NAMESPACE, LocateByUuidReport.class);

    @Override
    public ReportType getType() {
        return LOCATE_CORRESPONDING_NODE_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        Element workspace = info.getContentElement(DeltaVConstants.WORKSPACE.getName(), DeltaVConstants.WORKSPACE.getNamespace());
        String workspaceHref = this.normalizeResourceHref(DomUtil.getChildTextTrim(workspace, "href", DavConstants.NAMESPACE));
        if (workspaceHref == null || "".equals(workspaceHref)) {
            throw new DavException(400, "Request body must define the href of a source workspace");
        }
        try {
            this.correspHref = LocateCorrespondingNodeReport.getCorrespondingResourceHref(resource, this.getRepositorySession(), workspaceHref);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public Element toXml(Document document) {
        Element elem = DomUtil.createElement(document, "locate-corresponding-node-report", ItemResourceConstants.NAMESPACE);
        if (this.correspHref != null) {
            elem.appendChild(DomUtil.hrefToXml(this.correspHref, document));
        }
        return elem;
    }

    private static String getCorrespondingResourceHref(DavResource resource, Session session, String workspaceHref) throws RepositoryException {
        DavResourceLocator rLoc = resource.getLocator();
        String itemPath = rLoc.getRepositoryPath();
        Item item = session.getItem(itemPath);
        if (item.isNode()) {
            String workspaceName = rLoc.getFactory().createResourceLocator(rLoc.getPrefix(), workspaceHref).getWorkspaceName();
            String corrPath = ((Node)item).getCorrespondingNodePath(workspaceName);
            DavResourceLocator corrLoc = rLoc.getFactory().createResourceLocator(rLoc.getPrefix(), "/" + workspaceName, corrPath, false);
            return corrLoc.getHref(true);
        }
        throw new PathNotFoundException("Node with path " + itemPath + " does not exist.");
    }
}

