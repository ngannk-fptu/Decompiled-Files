/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.security.CurrentUserPrivilegeSetProperty;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JcrPrivilegeReport
extends AbstractJcrReport {
    private static Logger log = LoggerFactory.getLogger(JcrPrivilegeReport.class);
    public static final ReportType PRIVILEGES_REPORT = ReportType.register("privileges", ItemResourceConstants.NAMESPACE, JcrPrivilegeReport.class);
    private static final Privilege[] PRIVS = new Privilege[]{ItemResourceConstants.PRIVILEGE_JCR_READ, ItemResourceConstants.PRIVILEGE_JCR_ADD_NODE, ItemResourceConstants.PRIVILEGE_JCR_SET_PROPERTY, ItemResourceConstants.PRIVILEGE_JCR_REMOVE};
    private final MultiStatus ms = new MultiStatus();

    @Override
    public ReportType getType() {
        return PRIVILEGES_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return true;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        if (!info.containsContentElement("href", DavConstants.NAMESPACE)) {
            throw new DavException(400, "dcr:privileges element must at least contain a single DAV:href child.");
        }
        Element hrefElem = info.getContentElement("href", DavConstants.NAMESPACE);
        String href = DomUtil.getTextTrim(hrefElem);
        href = this.normalizeResourceHref(JcrPrivilegeReport.obtainAbsolutePathFromUri(href));
        DavResourceLocator resourceLoc = resource.getLocator();
        DavResourceLocator loc = resourceLoc.getFactory().createResourceLocator(resourceLoc.getPrefix(), href);
        this.addResponses(loc);
    }

    @Override
    public Element toXml(Document document) {
        return this.ms.toXml(document);
    }

    private void addResponses(DavResourceLocator locator) {
        String repositoryPath = locator.getRepositoryPath();
        MultiStatusResponse resp = new MultiStatusResponse(locator.getHref(false), null);
        ArrayList<Privilege> currentPrivs = new ArrayList<Privilege>();
        for (Privilege priv : PRIVS) {
            try {
                if (!this.getRepositorySession().hasPermission(repositoryPath, priv.getName())) continue;
                currentPrivs.add(priv);
            }
            catch (RepositoryException e) {
                log.debug(e.toString());
            }
        }
        resp.add(new CurrentUserPrivilegeSetProperty(currentPrivs.toArray(new Privilege[currentPrivs.size()])));
        this.ms.addResponse(resp);
    }

    private static String obtainAbsolutePathFromUri(String uri) {
        try {
            URI u = new URI(uri);
            StringBuilder sb = new StringBuilder();
            sb.append(u.getRawPath());
            if (u.getRawQuery() != null) {
                sb.append("?").append(u.getRawQuery());
            }
            return sb.toString();
        }
        catch (URISyntaxException ex) {
            log.warn("parsing " + uri, (Throwable)ex);
            return uri;
        }
    }
}

