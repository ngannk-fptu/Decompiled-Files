/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.ActivityResource;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionHistoryResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LatestActivityVersionReport
extends AbstractReport {
    private static Logger log = LoggerFactory.getLogger(LatestActivityVersionReport.class);
    private static final String XML_LATEST_ACTIVITY_VERSION = "latest-activity-version";
    private static final String XML_LATEST_ACTIVITY_VERSION_REPORT = "latest-activity-version-report";
    public static final ReportType LATEST_ACTIVITY_VERSION = ReportType.register("latest-activity-version", DeltaVConstants.NAMESPACE, LatestActivityVersionReport.class);
    private VersionHistoryResource vhResource;
    private DavResource activity;

    @Override
    public ReportType getType() {
        return LATEST_ACTIVITY_VERSION;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "DAV:latest-activity-version element expected.");
        }
        if (resource == null || !(resource instanceof VersionHistoryResource)) {
            throw new DavException(400, "DAV:latest-activity-version report can only be created for a version history resource.");
        }
        this.vhResource = (VersionHistoryResource)resource;
        String activityHref = this.normalizeResourceHref(DomUtil.getText(info.getContentElement("href", DavConstants.NAMESPACE)));
        DavResourceLocator vhLocator = resource.getLocator();
        DavResourceLocator activityLocator = vhLocator.getFactory().createResourceLocator(vhLocator.getPrefix(), activityHref);
        this.activity = resource.getFactory().createResource(activityLocator, resource.getSession());
        if (!(this.activity instanceof ActivityResource)) {
            throw new DavException(400, "DAV:latest-activity-version report: The DAV:href in the request body MUST identify an activity.");
        }
    }

    @Override
    public Element toXml(Document document) {
        String latestVersionHref = this.getLatestVersionHref();
        Element el = DomUtil.createElement(document, XML_LATEST_ACTIVITY_VERSION_REPORT, DeltaVConstants.NAMESPACE);
        el.appendChild(DomUtil.hrefToXml(latestVersionHref, document));
        return el;
    }

    private String getLatestVersionHref() {
        String latestVersionHref = "";
        try {
            List<String> versionHrefs = new HrefProperty(this.activity.getProperty(ActivityResource.ACTIVITY_VERSION_SET)).getHrefs();
            for (VersionResource vr : this.vhResource.getVersions()) {
                String href = vr.getHref();
                if (!versionHrefs.contains(href)) continue;
                if ("".equals(latestVersionHref)) {
                    latestVersionHref = href;
                    continue;
                }
                List<String> predecessors = new HrefProperty(vr.getProperty(VersionResource.PREDECESSOR_SET)).getHrefs();
                if (!predecessors.contains(latestVersionHref)) continue;
                latestVersionHref = href;
            }
        }
        catch (DavException e) {
            log.error("Unexpected error while retrieving href of latest version.", (Throwable)e);
        }
        return latestVersionHref;
    }
}

