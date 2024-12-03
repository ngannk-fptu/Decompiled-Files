/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LocateByHistoryReport
extends AbstractReport
implements DeltaVConstants {
    private static Logger log = LoggerFactory.getLogger(LocateByHistoryReport.class);
    private ReportInfo info;
    private Set<String> vhHrefSet = new HashSet<String>();
    private DavResource resource;

    @Override
    public ReportType getType() {
        return ReportType.LOCATE_BY_HISTORY;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (resource == null || !(resource instanceof VersionControlledResource)) {
            throw new DavException(400, "DAV:version-tree report can only be created for version-controlled resources and version resources.");
        }
        this.resource = resource;
        this.setInfo(info);
    }

    private void setInfo(ReportInfo info) throws DavException {
        if (info == null || !this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "DAV:locate-by-history element expected.");
        }
        Element versionHistorySet = info.getContentElement("version-history-set", NAMESPACE);
        if (versionHistorySet == null) {
            throw new DavException(400, "The DAV:locate-by-history element must contain a DAV:version-history-set child.");
        }
        ElementIterator it = DomUtil.getChildren(versionHistorySet, "href", DavConstants.NAMESPACE);
        while (it.hasNext()) {
            String href = DomUtil.getText(it.nextElement());
            if (href == null) continue;
            this.vhHrefSet.add(href);
        }
        this.info = info;
    }

    @Override
    public boolean isMultiStatusReport() {
        return true;
    }

    @Override
    public Element toXml(Document document) {
        return this.getMultiStatus().toXml(document);
    }

    private MultiStatus getMultiStatus() {
        MultiStatus ms = new MultiStatus();
        this.buildResponse(this.resource, this.info.getPropertyNameSet(), this.info.getDepth(), ms);
        return ms;
    }

    private void buildResponse(DavResource res, DavPropertyNameSet propNameSet, int depth, MultiStatus ms) {
        DavResourceIterator it = res.getMembers();
        while (!this.vhHrefSet.isEmpty() && it.hasNext()) {
            DavResource childRes = it.nextResource();
            if (childRes instanceof VersionControlledResource) {
                try {
                    VersionHistoryResource vhr = ((VersionControlledResource)childRes).getVersionHistory();
                    if (this.vhHrefSet.remove(vhr.getHref())) {
                        if (propNameSet.isEmpty()) {
                            ms.addResourceStatus(childRes, 200, 0);
                        } else {
                            ms.addResourceProperties(childRes, propNameSet, 0);
                        }
                    }
                }
                catch (DavException e) {
                    log.info(e.getMessage());
                }
            }
            if (depth <= 0) continue;
            this.buildResponse(it.nextResource(), propNameSet, depth - 1, ms);
        }
    }
}

