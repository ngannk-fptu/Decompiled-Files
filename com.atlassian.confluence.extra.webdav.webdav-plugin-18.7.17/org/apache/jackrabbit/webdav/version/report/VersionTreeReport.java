/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VersionTreeReport
extends AbstractReport
implements DeltaVConstants {
    private static Logger log = LoggerFactory.getLogger(VersionTreeReport.class);
    private ReportInfo info;
    private DavResource resource;

    @Override
    public ReportType getType() {
        return ReportType.VERSION_TREE;
    }

    @Override
    public boolean isMultiStatusReport() {
        return true;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        this.setResource(resource);
        this.setInfo(info);
    }

    private void setResource(DavResource resource) throws DavException {
        if (resource == null || !(resource instanceof VersionControlledResource) && !(resource instanceof VersionResource)) {
            throw new DavException(400, "DAV:version-tree report can only be created for version-controlled resources and version resources.");
        }
        this.resource = resource;
    }

    private void setInfo(ReportInfo info) throws DavException {
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "DAV:version-tree element expected.");
        }
        this.info = info;
    }

    @Override
    public Element toXml(Document document) {
        return this.getMultiStatus().toXml(document);
    }

    private MultiStatus getMultiStatus() {
        if (this.info == null || this.resource == null) {
            throw new NullPointerException("Error while running DAV:version-tree report");
        }
        MultiStatus ms = new MultiStatus();
        this.buildResponse(this.resource, this.info.getPropertyNameSet(), this.info.getDepth(), ms);
        return ms;
    }

    private void buildResponse(DavResource res, DavPropertyNameSet propNameSet, int depth, MultiStatus ms) {
        try {
            for (VersionResource version : VersionTreeReport.getVersions(res)) {
                if (propNameSet.isEmpty()) {
                    ms.addResourceStatus(version, 200, 0);
                    continue;
                }
                ms.addResourceProperties(version, propNameSet, 0);
            }
        }
        catch (DavException e) {
            log.error(e.toString());
        }
        if (depth > 0 && res.isCollection()) {
            DavResourceIterator it = res.getMembers();
            while (it.hasNext()) {
                this.buildResponse(it.nextResource(), propNameSet, depth - 1, ms);
            }
        }
    }

    private static VersionResource[] getVersions(DavResource res) throws DavException {
        VersionResource[] versions = new VersionResource[]{};
        if (res instanceof VersionControlledResource) {
            versions = ((VersionControlledResource)res).getVersionHistory().getVersions();
        } else if (res instanceof VersionResource) {
            versions = ((VersionResource)res).getVersionHistory().getVersions();
        }
        return versions;
    }
}

