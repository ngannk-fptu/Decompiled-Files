/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import java.util.HashMap;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.report.ExpandPropertyReport;
import org.apache.jackrabbit.webdav.version.report.LocateByHistoryReport;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.VersionTreeReport;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportType
implements DeltaVConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(ReportType.class);
    private static final HashMap<String, ReportType> types = new HashMap();
    public static final ReportType VERSION_TREE = ReportType.register("version-tree", NAMESPACE, VersionTreeReport.class);
    public static final ReportType EXPAND_PROPERTY = ReportType.register("expand-property", NAMESPACE, ExpandPropertyReport.class);
    public static final ReportType LOCATE_BY_HISTORY = ReportType.register("locate-by-history", NAMESPACE, LocateByHistoryReport.class);
    private final String key;
    private final String localName;
    private final Namespace namespace;
    private final Class<? extends Report> reportClass;

    private ReportType(String localName, Namespace namespace, String key, Class<? extends Report> reportClass) {
        this.localName = localName;
        this.namespace = namespace;
        this.key = key;
        this.reportClass = reportClass;
    }

    public Report createReport(DeltaVResource resource, ReportInfo info) throws DavException {
        try {
            Report report = this.reportClass.newInstance();
            report.init(resource, info);
            return report;
        }
        catch (IllegalAccessException e) {
            throw new DavException(500, "Failed to create new report (" + this.reportClass.getName() + ") from class: " + e.getMessage());
        }
        catch (InstantiationException e) {
            throw new DavException(500, "Failed to create new report (" + this.reportClass.getName() + ") from class: " + e.getMessage());
        }
    }

    @Override
    public Element toXml(Document document) {
        return DomUtil.createElement(document, this.localName, this.namespace);
    }

    public boolean isRequestedReportType(ReportInfo reqInfo) {
        if (reqInfo != null) {
            return this.getReportName().equals(reqInfo.getReportName());
        }
        return false;
    }

    public String getReportName() {
        return this.key;
    }

    public String getLocalName() {
        return this.localName;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public static ReportType register(String localName, Namespace namespace, Class<? extends Report> reportClass) {
        if (localName == null || namespace == null || reportClass == null) {
            throw new IllegalArgumentException("A ReportType cannot be registered with a null name, namespace or report class");
        }
        String key = DomUtil.getExpandedName(localName, namespace);
        if (types.containsKey(key)) {
            return types.get(key);
        }
        try {
            Report report = reportClass.newInstance();
            if (!(report instanceof Report)) {
                throw new IllegalArgumentException("Unable to register Report class: " + reportClass + " does not implement the Report interface.");
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Error while validating Report class: " + e.getMessage());
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Error while validating Report class.: " + e.getMessage());
        }
        ReportType type = new ReportType(localName, namespace, key, reportClass);
        types.put(key, type);
        return type;
    }

    public static ReportType getType(ReportInfo reportInfo) {
        if (reportInfo == null) {
            throw new IllegalArgumentException("ReportInfo must not be null.");
        }
        String key = reportInfo.getReportName();
        if (types.containsKey(key)) {
            return types.get(key);
        }
        throw new IllegalArgumentException("The request report '" + key + "' has not been registered yet.");
    }
}

