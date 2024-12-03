/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security.report;

import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchablePropertyReport
extends AbstractReport {
    public static final String REPORT_NAME = "principal-search-property-set";
    public static final ReportType REPORT_TYPE = ReportType.register("principal-search-property-set", SecurityConstants.NAMESPACE, SearchablePropertyReport.class);
    public static final String XML_PRINCIPAL_SEARCH_PROPERTY_SET = "principal-search-property-set";
    private final Set<PrincipalSearchProperty> searchPropertySet = new HashSet<PrincipalSearchProperty>();

    @Override
    public ReportType getType() {
        return REPORT_TYPE;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (resource == null || info == null) {
            throw new DavException(400, "Unable to run report: WebDAV Resource and ReportInfo must not be null.");
        }
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "Expected report type: '" + this.getType().getReportName() + "', found: '" + info.getReportName() + ";'.");
        }
        if (info.getDepth() > 0) {
            throw new DavException(400, "Invalid Depth header: " + info.getDepth());
        }
    }

    @Override
    public Element toXml(Document document) {
        Element rootElem = DomUtil.createElement(document, "principal-search-property-set", SecurityConstants.NAMESPACE);
        for (PrincipalSearchProperty psp : this.searchPropertySet) {
            rootElem.appendChild(psp.toXml(document));
        }
        return rootElem;
    }

    public void addPrincipalSearchProperty(DavPropertyName propName, String description, String language) {
        this.searchPropertySet.add(new PrincipalSearchProperty(propName, description, language));
    }

    private class PrincipalSearchProperty
    implements XmlSerializable {
        private static final String XML_PRINCIPAL_SEARCH_PROPERTY = "principal-search-property";
        private static final String XML_DESCRIPTION = "description";
        private static final String ATTR_LANG = "lang";
        private final DavPropertyName propName;
        private final String description;
        private final String language;
        private final int hashCode;

        private PrincipalSearchProperty(DavPropertyName propName, String description, String language) {
            if (propName == null) {
                throw new IllegalArgumentException("null is not a valid DavPropertyName for the DAV:principal-search-property.");
            }
            this.propName = propName;
            this.description = description;
            this.language = language;
            this.hashCode = propName.hashCode();
        }

        @Override
        public Element toXml(Document document) {
            Element psElem = DomUtil.createElement(document, XML_PRINCIPAL_SEARCH_PROPERTY, SecurityConstants.NAMESPACE);
            DavPropertyNameSet pnSet = new DavPropertyNameSet();
            pnSet.add(this.propName);
            psElem.appendChild(pnSet.toXml(document));
            if (this.description != null) {
                Element desc = DomUtil.addChildElement(psElem, XML_DESCRIPTION, SecurityConstants.NAMESPACE, this.description);
                if (this.language != null) {
                    DomUtil.setAttribute(desc, ATTR_LANG, Namespace.XML_NAMESPACE, this.language);
                }
            }
            return psElem;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof PrincipalSearchProperty) {
                PrincipalSearchProperty other = (PrincipalSearchProperty)obj;
                return this.hashCode == other.hashCode;
            }
            return false;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

