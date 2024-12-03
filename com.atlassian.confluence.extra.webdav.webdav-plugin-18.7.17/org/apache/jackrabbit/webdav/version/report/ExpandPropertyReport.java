/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExpandPropertyReport
extends AbstractReport
implements DeltaVConstants {
    private static Logger log = LoggerFactory.getLogger(ExpandPropertyReport.class);
    private DavResource resource;
    private ReportInfo info;
    private Iterator<Element> propertyElements;

    @Override
    public ReportType getType() {
        return ReportType.EXPAND_PROPERTY;
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
        if (resource == null) {
            throw new DavException(400, "The resource specified must not be null.");
        }
        this.resource = resource;
    }

    private void setInfo(ReportInfo info) throws DavException {
        if (info == null) {
            throw new DavException(400, "The report info specified must not be null.");
        }
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "DAV:expand-property element expected.");
        }
        this.info = info;
        this.propertyElements = info.getContentElements("property", NAMESPACE).iterator();
    }

    @Override
    public Element toXml(Document document) {
        return this.getMultiStatus().toXml(document);
    }

    private MultiStatus getMultiStatus() {
        MultiStatus ms = new MultiStatus();
        this.addResponses(this.resource, this.info.getDepth(), ms);
        return ms;
    }

    private void addResponses(DavResource res, int depth, MultiStatus ms) {
        MultiStatusResponse response = this.getResponse(res, this.propertyElements);
        ms.addResponse(response);
        if (depth > 0 && res.isCollection()) {
            DavResourceIterator it = res.getMembers();
            while (it.hasNext()) {
                this.addResponses(it.nextResource(), depth - 1, ms);
            }
        }
    }

    private MultiStatusResponse getResponse(DavResource res, Iterator<Element> propertyElements) {
        MultiStatusResponse resp = new MultiStatusResponse(res.getHref(), null);
        while (propertyElements.hasNext()) {
            Element propertyElem = propertyElements.next();
            String nameAttr = propertyElem.getAttribute("name");
            if (nameAttr == null || "".equals(nameAttr)) continue;
            String namespaceAttr = propertyElem.getAttribute("namespace");
            Namespace namespace = namespaceAttr != null ? Namespace.getNamespace(namespaceAttr) : NAMESPACE;
            DavPropertyName propName = DavPropertyName.create(nameAttr, namespace);
            DavProperty<?> p = res.getProperty(propName);
            if (p != null) {
                if (p instanceof HrefProperty && res instanceof DeltaVResource) {
                    ElementIterator it = DomUtil.getChildren(propertyElem, "property", NAMESPACE);
                    resp.add(new ExpandProperty((DeltaVResource)res, (HrefProperty)p, it));
                    continue;
                }
                resp.add(p);
                continue;
            }
            resp.add(propName, 404);
        }
        return resp;
    }

    private class ExpandProperty
    extends AbstractDavProperty<List<MultiStatusResponse>> {
        private List<MultiStatusResponse> valueList;

        private ExpandProperty(DeltaVResource deltaVResource, HrefProperty hrefProperty, ElementIterator elementIter) {
            super(hrefProperty.getName(), hrefProperty.isInvisibleInAllprop());
            this.valueList = new ArrayList<MultiStatusResponse>();
            try {
                DavResource[] refResource;
                for (DavResource res : refResource = deltaVResource.getReferenceResources(hrefProperty.getName())) {
                    MultiStatusResponse resp = ExpandPropertyReport.this.getResponse(res, elementIter);
                    this.valueList.add(resp);
                }
            }
            catch (DavException e) {
                log.error(e.getMessage());
            }
        }

        @Override
        public List<MultiStatusResponse> getValue() {
            return this.valueList;
        }
    }
}

