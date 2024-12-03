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
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ReportInfo
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(ReportInfo.class);
    private final String typeLocalName;
    private final Namespace typeNamespace;
    private final int depth;
    private final DavPropertyNameSet propertyNames;
    private final List<Element> content = new ArrayList<Element>();

    public ReportInfo(ReportType type) {
        this(type, 0, null);
    }

    public ReportInfo(ReportType type, int depth) {
        this(type, depth, null);
    }

    public ReportInfo(ReportType type, int depth, DavPropertyNameSet propertyNames) {
        this(type.getLocalName(), type.getNamespace(), depth, propertyNames);
    }

    public ReportInfo(String typeLocalName, Namespace typeNamespace) {
        this(typeLocalName, typeNamespace, 0, null);
    }

    public ReportInfo(String typelocalName, Namespace typeNamespace, int depth, DavPropertyNameSet propertyNames) {
        this.typeLocalName = typelocalName;
        this.typeNamespace = typeNamespace;
        this.depth = depth;
        this.propertyNames = propertyNames != null ? new DavPropertyNameSet(propertyNames) : new DavPropertyNameSet();
    }

    public ReportInfo(Element reportElement, int depth) throws DavException {
        if (reportElement == null) {
            log.warn("Report request body must not be null.");
            throw new DavException(400);
        }
        this.typeLocalName = reportElement.getLocalName();
        this.typeNamespace = DomUtil.getNamespace(reportElement);
        this.depth = depth;
        Element propElement = DomUtil.getChildElement(reportElement, "prop", DavConstants.NAMESPACE);
        if (propElement != null) {
            this.propertyNames = new DavPropertyNameSet(propElement);
            reportElement.removeChild(propElement);
        } else {
            this.propertyNames = new DavPropertyNameSet();
        }
        ElementIterator it = DomUtil.getChildren(reportElement);
        while (it.hasNext()) {
            Element el = it.nextElement();
            if ("prop".equals(el.getLocalName())) continue;
            this.content.add(el);
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public String getReportName() {
        return DomUtil.getExpandedName(this.typeLocalName, this.typeNamespace);
    }

    public boolean containsContentElement(String localName, Namespace namespace) {
        if (this.content.isEmpty()) {
            return false;
        }
        for (Element elem : this.content) {
            boolean bl = namespace == null ? elem.getNamespaceURI() == null : namespace.isSame(elem.getNamespaceURI());
            boolean sameNamespace = bl;
            if (!sameNamespace || !elem.getLocalName().equals(localName)) continue;
            return true;
        }
        return false;
    }

    public Element getContentElement(String localName, Namespace namespace) {
        List<Element> values = this.getContentElements(localName, namespace);
        if (values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public List<Element> getContentElements(String localName, Namespace namespace) {
        ArrayList<Element> l = new ArrayList<Element>();
        for (Element elem : this.content) {
            if (!DomUtil.matches(elem, localName, namespace)) continue;
            l.add(elem);
        }
        return l;
    }

    public void setContentElement(Element contentElement) {
        this.content.add(contentElement);
    }

    public DavPropertyNameSet getPropertyNameSet() {
        return this.propertyNames;
    }

    @Override
    public Element toXml(Document document) {
        Element reportElement = DomUtil.createElement(document, this.typeLocalName, this.typeNamespace);
        if (!this.content.isEmpty()) {
            for (Element contentEntry : this.content) {
                Node n = document.importNode(contentEntry, true);
                reportElement.appendChild(n);
            }
        }
        if (!this.propertyNames.isEmpty()) {
            reportElement.appendChild(this.propertyNames.toXml(document));
        }
        return reportElement;
    }
}

