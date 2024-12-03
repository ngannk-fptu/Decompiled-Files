/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security.report;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.report.AbstractSecurityReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Element;

public class PrincipalMatchReport
extends AbstractSecurityReport {
    public static final String XML_PRINCIPAL_PROPERTY = "principal-property";
    public static final String XML_SELF = "self";
    public static final String REPORT_NAME = "principal-match";
    public static final ReportType REPORT_TYPE = ReportType.register("principal-match", SecurityConstants.NAMESPACE, PrincipalMatchReport.class);
    private DavPropertyName principalPropertyName;

    @Override
    public ReportType getType() {
        return REPORT_TYPE;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        if (info.containsContentElement(XML_PRINCIPAL_PROPERTY, SecurityConstants.NAMESPACE)) {
            Element pp = info.getContentElement(XML_PRINCIPAL_PROPERTY, SecurityConstants.NAMESPACE);
            this.principalPropertyName = DavPropertyName.createFromXml(DomUtil.getFirstChildElement(pp));
        } else if (info.containsContentElement(XML_SELF, SecurityConstants.NAMESPACE)) {
            this.principalPropertyName = SecurityConstants.PRINCIPAL_URL;
        } else {
            throw new DavException(400, "DAV:self or DAV:principal-property element required within report info.");
        }
    }

    public DavPropertyName getPrincipalPropertyName() {
        return this.principalPropertyName;
    }

    public void setResponses(MultiStatusResponse[] responses) {
        this.responses = responses;
    }
}

