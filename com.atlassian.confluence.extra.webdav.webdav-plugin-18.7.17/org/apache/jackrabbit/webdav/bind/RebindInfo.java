/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.bind;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.bind.BindConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RebindInfo
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(RebindInfo.class);
    private String segment;
    private String href;

    public RebindInfo(String href, String segment) {
        this.href = href;
        this.segment = segment;
    }

    public String getHref() {
        return this.href;
    }

    public String getSegment() {
        return this.segment;
    }

    public static RebindInfo createFromXml(Element root) throws DavException {
        if (!DomUtil.matches(root, "rebind", BindConstants.NAMESPACE)) {
            log.warn("DAV:rebind element expected");
            throw new DavException(400);
        }
        String href = null;
        String segment = null;
        ElementIterator it = DomUtil.getChildren(root);
        while (it.hasNext()) {
            Element elt = it.nextElement();
            if (DomUtil.matches(elt, "segment", BindConstants.NAMESPACE)) {
                if (segment == null) {
                    segment = DomUtil.getText(elt);
                    continue;
                }
                log.warn("unexpected multiple occurrence of DAV:segment element");
                throw new DavException(400);
            }
            if (DomUtil.matches(elt, "href", BindConstants.NAMESPACE)) {
                if (href == null) {
                    href = DomUtil.getText(elt);
                    continue;
                }
                log.warn("unexpected multiple occurrence of DAV:href element");
                throw new DavException(400);
            }
            log.warn("unexpected element " + elt.getLocalName());
            throw new DavException(400);
        }
        if (href == null) {
            log.warn("DAV:href element expected");
            throw new DavException(400);
        }
        if (segment == null) {
            log.warn("DAV:segment element expected");
            throw new DavException(400);
        }
        return new RebindInfo(href, segment);
    }

    @Override
    public Element toXml(Document document) {
        Element rebindElt = DomUtil.createElement(document, "rebind", BindConstants.NAMESPACE);
        Element hrefElt = DomUtil.createElement(document, "href", BindConstants.NAMESPACE, this.href);
        Element segElt = DomUtil.createElement(document, "segment", BindConstants.NAMESPACE, this.segment);
        rebindElt.appendChild(hrefElt);
        rebindElt.appendChild(segElt);
        return rebindElt;
    }
}

