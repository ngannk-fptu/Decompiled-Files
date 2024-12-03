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
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SupportedReportSetProperty
extends AbstractDavProperty<Set<ReportType>> {
    private static Logger log = LoggerFactory.getLogger(SupportedReportSetProperty.class);
    private final Set<ReportType> reportTypes = new HashSet<ReportType>();

    public SupportedReportSetProperty() {
        super(DeltaVConstants.SUPPORTED_REPORT_SET, true);
    }

    public SupportedReportSetProperty(ReportType[] reportTypes) {
        super(DeltaVConstants.SUPPORTED_REPORT_SET, true);
        for (ReportType reportType : reportTypes) {
            this.addReportType(reportType);
        }
    }

    public void addReportType(ReportType reportType) {
        this.reportTypes.add(reportType);
    }

    public boolean isSupportedReport(ReportInfo reqInfo) {
        for (ReportType reportType : this.reportTypes) {
            if (!reportType.isRequestedReportType(reqInfo)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<ReportType> getValue() {
        return this.reportTypes;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (ReportType rt : this.reportTypes) {
            Element sr = DomUtil.addChildElement(elem, "supported-report", DeltaVConstants.NAMESPACE);
            Element r = DomUtil.addChildElement(sr, "report", DeltaVConstants.NAMESPACE);
            r.appendChild(rt.toXml(document));
        }
        return elem;
    }
}

