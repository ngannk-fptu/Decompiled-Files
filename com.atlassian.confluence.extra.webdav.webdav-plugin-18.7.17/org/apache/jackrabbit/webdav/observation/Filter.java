/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.observation;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Filter
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(Filter.class);
    private final String filterName;
    private final Namespace filterNamespace;
    private final String filterValue;

    public Filter(String filterName, Namespace filterNamespace, String filterValue) {
        if (filterName == null) {
            throw new IllegalArgumentException("filterName must not be null.");
        }
        this.filterName = filterName;
        this.filterNamespace = filterNamespace;
        this.filterValue = filterValue;
    }

    public Filter(Element filterElem) {
        this.filterName = filterElem.getLocalName();
        this.filterNamespace = DomUtil.getNamespace(filterElem);
        this.filterValue = DomUtil.getTextTrim(filterElem);
    }

    public String getName() {
        return this.filterName;
    }

    public Namespace getNamespace() {
        return this.filterNamespace;
    }

    public String getValue() {
        return this.filterValue;
    }

    public boolean isMatchingFilter(String localName, Namespace namespace) {
        boolean matchingNsp = this.filterNamespace == null ? namespace == null : this.filterNamespace.equals(namespace);
        return this.filterName.equals(localName) && matchingNsp;
    }

    @Override
    public Element toXml(Document document) {
        return DomUtil.createElement(document, this.filterName, this.filterNamespace, this.filterValue);
    }
}

