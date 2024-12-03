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

public class UnbindInfo
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(UnbindInfo.class);
    private String segment;

    private UnbindInfo() {
    }

    public UnbindInfo(String segment) {
        this.segment = segment;
    }

    public String getSegment() {
        return this.segment;
    }

    public static UnbindInfo createFromXml(Element root) throws DavException {
        if (!DomUtil.matches(root, "unbind", BindConstants.NAMESPACE)) {
            log.warn("DAV:unbind element expected");
            throw new DavException(400);
        }
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
            log.warn("unexpected element " + elt.getLocalName());
            throw new DavException(400);
        }
        if (segment == null) {
            log.warn("DAV:segment element expected");
            throw new DavException(400);
        }
        return new UnbindInfo(segment);
    }

    @Override
    public Element toXml(Document document) {
        Element unbindElt = DomUtil.createElement(document, "unbind", BindConstants.NAMESPACE);
        Element segElt = DomUtil.createElement(document, "segment", BindConstants.NAMESPACE, this.segment);
        unbindElt.appendChild(segElt);
        return unbindElt;
    }
}

