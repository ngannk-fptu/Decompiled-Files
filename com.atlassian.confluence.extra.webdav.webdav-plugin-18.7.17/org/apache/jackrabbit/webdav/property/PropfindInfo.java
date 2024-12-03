/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PropfindInfo
implements XmlSerializable {
    private final int propfindType;
    private final DavPropertyNameSet propNameSet;

    public PropfindInfo(int propfindType, DavPropertyNameSet propNameSet) {
        this.propfindType = propfindType;
        this.propNameSet = propNameSet;
    }

    @Override
    public Element toXml(Document document) {
        Element propfind = DomUtil.createElement(document, "propfind", DavConstants.NAMESPACE);
        switch (this.propfindType) {
            case 1: {
                propfind.appendChild(DomUtil.createElement(document, "allprop", DavConstants.NAMESPACE));
                break;
            }
            case 2: {
                propfind.appendChild(DomUtil.createElement(document, "propname", DavConstants.NAMESPACE));
                break;
            }
            case 0: {
                if (this.propNameSet == null) {
                    Element prop = DomUtil.createElement(document, "prop", DavConstants.NAMESPACE);
                    Element resourcetype = DomUtil.createElement(document, "resourcetype", DavConstants.NAMESPACE);
                    prop.appendChild(resourcetype);
                    propfind.appendChild(prop);
                    break;
                }
                propfind.appendChild(this.propNameSet.toXml(document));
                break;
            }
            case 3: {
                propfind.appendChild(DomUtil.createElement(document, "allprop", DavConstants.NAMESPACE));
                if (this.propNameSet == null || this.propNameSet.isEmpty()) break;
                Element include = DomUtil.createElement(document, "include", DavConstants.NAMESPACE);
                Element prop = this.propNameSet.toXml(document);
                for (Node c = prop.getFirstChild(); c != null; c = c.getNextSibling()) {
                    include.appendChild(c.cloneNode(true));
                }
                propfind.appendChild(include);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown propfind type");
            }
        }
        return propfind;
    }
}

