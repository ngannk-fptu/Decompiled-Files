/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.security.report;

import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.report.AbstractSecurityReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class PrincipalSearchReport
extends AbstractSecurityReport {
    private static Logger log = LoggerFactory.getLogger(PrincipalSearchReport.class);
    public static final String XML_APPLY_TO_PRINCIPAL_COLLECTION_SET = "apply-to-principal-collection-set";
    public static final String XML_PROPERTY_SEARCH = "property-search";
    public static final String XML_MATCH = "match";
    public static final String REPORT_NAME = "principal-property-search";
    public static final ReportType REPORT_TYPE = ReportType.register("principal-property-search", SecurityConstants.NAMESPACE, PrincipalSearchReport.class);
    private String[] searchRoots;
    private SearchArgument[] searchArguments;

    @Override
    public ReportType getType() {
        return REPORT_TYPE;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
        if (!info.containsContentElement(XML_PROPERTY_SEARCH, SecurityConstants.NAMESPACE)) {
            throw new DavException(400, "Request body must contain at least a single DAV:property-search element.");
        }
        List<Element> psElements = info.getContentElements(XML_PROPERTY_SEARCH, SecurityConstants.NAMESPACE);
        this.searchArguments = new SearchArgument[psElements.size()];
        int i = 0;
        for (Element psElement : psElements) {
            this.searchArguments[i++] = new SearchArgument(psElement);
        }
        if (info.containsContentElement(XML_APPLY_TO_PRINCIPAL_COLLECTION_SET, SecurityConstants.NAMESPACE)) {
            HrefProperty p = new HrefProperty(resource.getProperty(SecurityConstants.PRINCIPAL_COLLECTION_SET));
            this.searchRoots = p.getHrefs().toArray(new String[0]);
        } else {
            this.searchRoots = new String[]{resource.getHref()};
        }
    }

    public String[] getSearchRoots() {
        return this.searchRoots;
    }

    public SearchArgument[] getSearchArguments() {
        return this.searchArguments;
    }

    public void setResponses(MultiStatusResponse[] responses) {
        this.responses = responses;
    }

    protected class SearchArgument {
        private final DavPropertyNameSet searchProps;
        private final String searchString;

        private SearchArgument(Element propSearch) {
            this.searchProps = new DavPropertyNameSet(DomUtil.getChildElement(propSearch, "prop", DavConstants.NAMESPACE));
            this.searchString = DomUtil.getChildText(propSearch, PrincipalSearchReport.XML_MATCH, SecurityConstants.NAMESPACE);
        }

        protected DavPropertyNameSet getSearchProperties() {
            return this.searchProps;
        }

        protected String getSearchString() {
            return this.searchString;
        }
    }
}

