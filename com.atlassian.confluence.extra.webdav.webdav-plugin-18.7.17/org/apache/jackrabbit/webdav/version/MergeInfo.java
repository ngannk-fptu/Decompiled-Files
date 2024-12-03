/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MergeInfo
implements DeltaVConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(MergeInfo.class);
    private final Element mergeElement;
    private final DavPropertyNameSet propertyNameSet;

    public MergeInfo(Element mergeElement) throws DavException {
        if (!DomUtil.matches(mergeElement, "merge", NAMESPACE)) {
            log.warn("'DAV:merge' element expected");
            throw new DavException(400);
        }
        Element propElem = DomUtil.getChildElement(mergeElement, "prop", DavConstants.NAMESPACE);
        if (propElem != null) {
            this.propertyNameSet = new DavPropertyNameSet(propElem);
            mergeElement.removeChild(propElem);
        } else {
            this.propertyNameSet = new DavPropertyNameSet();
        }
        this.mergeElement = mergeElement;
    }

    public String[] getSourceHrefs() {
        ArrayList<String> sourceHrefs = new ArrayList<String>();
        Element srcElem = DomUtil.getChildElement(this.mergeElement, "source", DavConstants.NAMESPACE);
        if (srcElem != null) {
            ElementIterator it = DomUtil.getChildren(srcElem, "href", DavConstants.NAMESPACE);
            while (it.hasNext()) {
                String href = DomUtil.getTextTrim(it.nextElement());
                if (href == null) continue;
                sourceHrefs.add(href);
            }
        }
        return sourceHrefs.toArray(new String[sourceHrefs.size()]);
    }

    public boolean isNoAutoMerge() {
        return DomUtil.hasChildElement(this.mergeElement, "no-auto-merge", NAMESPACE);
    }

    public boolean isNoCheckout() {
        return DomUtil.hasChildElement(this.mergeElement, "no-checkout", NAMESPACE);
    }

    public DavPropertyNameSet getPropertyNameSet() {
        return this.propertyNameSet;
    }

    public Element getMergeElement() {
        return this.mergeElement;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = (Element)document.importNode(this.mergeElement, true);
        if (!this.propertyNameSet.isEmpty()) {
            elem.appendChild(this.propertyNameSet.toXml(document));
        }
        return elem;
    }

    public static Element createMergeElement(String[] mergeSource, boolean isNoAutoMerge, boolean isNoCheckout, Document factory) {
        Element mergeElem = DomUtil.createElement(factory, "merge", NAMESPACE);
        Element source = DomUtil.addChildElement(mergeElem, "source", DavConstants.NAMESPACE);
        for (String ms : mergeSource) {
            source.appendChild(DomUtil.hrefToXml(ms, factory));
        }
        if (isNoAutoMerge) {
            DomUtil.addChildElement(mergeElem, "no-auto-merge", NAMESPACE);
        }
        if (isNoCheckout) {
            DomUtil.addChildElement(mergeElem, "no-checkout", NAMESPACE);
        }
        return mergeElem;
    }
}

