/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.util.ArrayList;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.simple.DavResourceImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DeltaVResourceImpl
extends DavResourceImpl
implements DeltaVResource {
    protected SupportedReportSetProperty supportedReports = new SupportedReportSetProperty();
    private static final Logger log = LoggerFactory.getLogger(DeltaVResourceImpl.class);
    private static final String DELTAV_COMPLIANCE_CLASSES = DavCompliance.concatComplianceClasses(new String[]{DavResourceImpl.COMPLIANCE_CLASSES, "bind"});

    public DeltaVResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Item item) throws DavException {
        super(locator, factory, session, config, (Node)item);
        this.initSupportedReports();
    }

    public DeltaVResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, boolean isCollection) throws DavException {
        super(locator, factory, session, config, isCollection);
        this.initSupportedReports();
    }

    @Override
    public String getComplianceClass() {
        return DELTAV_COMPLIANCE_CLASSES;
    }

    @Override
    public OptionsResponse getOptionResponse(OptionsInfo optionsInfo) {
        OptionsResponse oR = null;
        if (optionsInfo != null) {
            oR = new OptionsResponse();
            if (optionsInfo.containsElement("version-history-collection-set", DeltaVConstants.NAMESPACE)) {
                String[] hrefs = new String[]{this.getLocatorFromNodePath("/jcr:system/jcr:versionStorage").getHref(true)};
                oR.addEntry("version-history-collection-set", DeltaVConstants.NAMESPACE, hrefs);
            }
        }
        return oR;
    }

    @Override
    public Report getReport(ReportInfo reportInfo) throws DavException {
        if (reportInfo == null) {
            throw new DavException(400, "A REPORT request must provide a valid XML request body.");
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (!this.supportedReports.isSupportedReport(reportInfo)) {
            Element condition = null;
            try {
                condition = DomUtil.createDocument().createElementNS("DAV:", "supported-report");
            }
            catch (ParserConfigurationException parserConfigurationException) {
                // empty catch block
            }
            throw new DavException(409, "Unknown report '" + reportInfo.getReportName() + "' requested.", null, condition);
        }
        return ReportType.getType(reportInfo).createReport(this, reportInfo);
    }

    @Override
    public void addWorkspace(DavResource workspace) throws DavException {
        throw new DavException(403);
    }

    @Override
    public DavResource[] getReferenceResources(DavPropertyName hrefPropertyName) throws DavException {
        DavProperty<?> prop = this.getProperty(hrefPropertyName);
        ArrayList<DavResource> resources = new ArrayList<DavResource>();
        if (prop != null && prop instanceof HrefProperty) {
            HrefProperty hp = (HrefProperty)prop;
            for (String href : hp.getHrefs()) {
                DavResourceLocator locator = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), href);
                resources.add(this.createResourceFromLocator(locator));
            }
        } else {
            throw new DavException(500);
        }
        return resources.toArray(new DavResource[resources.size()]);
    }

    protected DavResourceLocator getLocatorFromNodePath(String nodePath) {
        DavResourceLocator loc = this.getLocator().getFactory().createResourceLocator(this.getLocator().getPrefix(), this.getLocator().getWorkspacePath(), nodePath, false);
        return loc;
    }

    protected DavResourceLocator getLocatorFromNode(Node repositoryNode) {
        String nodePath = null;
        try {
            if (repositoryNode != null) {
                nodePath = repositoryNode.getPath();
            }
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
        }
        return this.getLocatorFromNodePath(nodePath);
    }

    protected DavResource createResourceFromLocator(DavResourceLocator loc) throws DavException {
        DavResource res = this.getFactory().createResource(loc, this.getSession());
        return res;
    }

    protected HrefProperty getHrefProperty(DavPropertyName name, Node[] values, boolean isProtected, boolean isCollection) {
        if (values == null) {
            return null;
        }
        String[] pHref = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            pHref[i] = this.getLocatorFromNode(values[i]).getHref(isCollection);
        }
        return new HrefProperty(name, pHref, isProtected);
    }

    protected void initSupportedReports() {
        if (this.exists()) {
            this.supportedReports.addReportType(ReportType.EXPAND_PROPERTY);
            if (this.isCollection()) {
                this.supportedReports.addReportType(ReportType.LOCATE_BY_HISTORY);
            }
        }
    }

    @Override
    protected void initProperties() {
        if (!this.propsInitialized) {
            super.initProperties();
            if (this.exists()) {
                this.properties.add(this.supportedReports);
                Node n = this.getNode();
                try {
                    if (n.hasProperty("{http://www.jcp.org/jcr/1.0}createdBy")) {
                        String createdBy = n.getProperty("{http://www.jcp.org/jcr/1.0}createdBy").getString();
                        this.properties.add(new DefaultDavProperty<String>(DeltaVConstants.CREATOR_DISPLAYNAME, createdBy, true));
                    }
                }
                catch (RepositoryException e) {
                    log.debug("Error while accessing jcr:createdBy property");
                }
            }
        }
    }
}

