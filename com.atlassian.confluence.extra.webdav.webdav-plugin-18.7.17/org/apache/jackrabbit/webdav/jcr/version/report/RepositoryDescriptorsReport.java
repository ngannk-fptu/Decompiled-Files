/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.version.report.AbstractJcrReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RepositoryDescriptorsReport
extends AbstractJcrReport
implements ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(RepositoryDescriptorsReport.class);
    public static final ReportType REPOSITORY_DESCRIPTORS_REPORT = ReportType.register("repositorydescriptors", ItemResourceConstants.NAMESPACE, RepositoryDescriptorsReport.class);

    @Override
    public ReportType getType() {
        return REPOSITORY_DESCRIPTORS_REPORT;
    }

    @Override
    public boolean isMultiStatusReport() {
        return false;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        super.init(resource, info);
    }

    @Override
    public Element toXml(Document document) {
        Repository repository = this.getRepositorySession().getRepository();
        Element report = DomUtil.createElement(document, "repositorydescriptors-report", NAMESPACE);
        for (String key : repository.getDescriptorKeys()) {
            Element elem = DomUtil.addChildElement(report, "descriptor", NAMESPACE);
            DomUtil.addChildElement(elem, "descriptorkey", NAMESPACE, key);
            for (Value v : repository.getDescriptorValues(key)) {
                String value;
                try {
                    value = v.getString();
                }
                catch (RepositoryException e) {
                    log.error("Internal error while reading descriptor value: ", (Throwable)e);
                    value = repository.getDescriptor(key);
                }
                Element child = DomUtil.addChildElement(elem, "descriptorvalue", NAMESPACE, value);
                if (1 == v.getType()) continue;
                child.setAttribute("type", PropertyType.nameFromValue(v.getType()));
            }
        }
        return report;
    }
}

