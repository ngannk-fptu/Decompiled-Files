/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.BaselineResource;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.version.VersionResource;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CompareBaselineReport
extends AbstractReport {
    private static Logger log = LoggerFactory.getLogger(CompareBaselineReport.class);
    private static final String XML_COMPARE_BASELINE = "compare-baseline";
    private static final String XML_COMPARE_BASELINE_REPORT = "compare-baseline-report";
    private static final String XML_ADDED_VERSION = "added-version";
    private static final String XML_DELETED_VERSION = "deleted-version";
    private static final String XML_CHANGED_VERSION = "changed-version";
    public static final ReportType COMPARE_BASELINE = ReportType.register("compare-baseline", DeltaVConstants.NAMESPACE, CompareBaselineReport.class);
    private BaselineResource requestBaseline;
    private BaselineResource compareBaseline;

    @Override
    public ReportType getType() {
        return COMPARE_BASELINE;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "DAV:compare-baseline element expected.");
        }
        if (resource == null || !(resource instanceof BaselineResource)) {
            throw new DavException(400, "DAV:compare-baseline report can only be created for a baseline resource.");
        }
        this.requestBaseline = (BaselineResource)resource;
        String compareHref = this.normalizeResourceHref(DomUtil.getText(info.getContentElement("href", DavConstants.NAMESPACE)));
        DavResourceLocator locator = resource.getLocator();
        DavResourceLocator compareLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), compareHref);
        DavResource compRes = resource.getFactory().createResource(compareLocator, resource.getSession());
        if (!(compRes instanceof BaselineResource)) {
            throw new DavException(400, "DAV:latest-activity-version report: The DAV:href in the request body MUST identify an activity.");
        }
        this.compareBaseline = (BaselineResource)compRes;
    }

    @Override
    public Element toXml(Document document) {
        Element el = DomUtil.createElement(document, XML_COMPARE_BASELINE_REPORT, DeltaVConstants.NAMESPACE);
        try {
            ArrayList<VersionResource> requestVs = new ArrayList<VersionResource>();
            this.getVersions(this.requestBaseline.getBaselineCollection(), requestVs);
            ArrayList<VersionResource> compareVs = new ArrayList<VersionResource>();
            this.getVersions(this.compareBaseline.getBaselineCollection(), compareVs);
            for (VersionResource requestV : requestVs) {
                Element cv;
                if (compareVs.remove(requestV)) continue;
                VersionResource changedV = this.findChangedVersion(requestV, compareVs);
                if (changedV != null) {
                    cv = DomUtil.addChildElement(el, XML_CHANGED_VERSION, DeltaVConstants.NAMESPACE);
                    cv.appendChild(DomUtil.hrefToXml(requestV.getHref(), document));
                    cv.appendChild(DomUtil.hrefToXml(changedV.getHref(), document));
                    continue;
                }
                cv = DomUtil.addChildElement(el, XML_DELETED_VERSION, DeltaVConstants.NAMESPACE);
                cv.appendChild(DomUtil.hrefToXml(requestV.getHref(), document));
            }
            for (VersionResource addedV : compareVs) {
                Element cv = DomUtil.addChildElement(el, XML_ADDED_VERSION, DeltaVConstants.NAMESPACE);
                cv.appendChild(DomUtil.hrefToXml(addedV.getHref(), document));
            }
        }
        catch (DavException e) {
            log.error("Internal error while building report", (Throwable)e);
        }
        return el;
    }

    private void getVersions(DavResource collection, List<VersionResource> vList) throws DavException {
        DavResourceIterator it = collection.getMembers();
        while (it.hasNext()) {
            DavResource member = it.nextResource();
            if (member instanceof VersionControlledResource) {
                String href = new HrefProperty(member.getProperty(VersionControlledResource.CHECKED_IN)).getHrefs().get(0);
                DavResourceLocator locator = member.getLocator();
                DavResourceLocator vLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), href);
                DavResource v = member.getFactory().createResource(vLocator, member.getSession());
                if (v instanceof VersionResource) {
                    vList.add((VersionResource)v);
                } else {
                    log.error("Internal error: DAV:checked-in property must point to a VersionResource.");
                }
            }
            if (!member.isCollection()) continue;
            this.getVersions(member, vList);
        }
    }

    private VersionResource findChangedVersion(VersionResource requestV, List<VersionResource> compareVs) throws DavException {
        VersionResource[] vs;
        for (VersionResource v : vs = requestV.getVersionHistory().getVersions()) {
            if (!compareVs.remove(v)) continue;
            return v;
        }
        return null;
    }
}

