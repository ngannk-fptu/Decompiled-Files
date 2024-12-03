/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OptionsInfo
implements XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(OptionsInfo.class);
    private final Set<String> entriesLocalNames = new HashSet<String>();

    public OptionsInfo(String[] entriesLocalNames) {
        if (entriesLocalNames != null) {
            this.entriesLocalNames.addAll(Arrays.asList(entriesLocalNames));
        }
    }

    private OptionsInfo() {
    }

    public boolean containsElement(String localName, Namespace namespace) {
        if (DeltaVConstants.NAMESPACE.equals(namespace)) {
            return this.entriesLocalNames.contains(localName);
        }
        return false;
    }

    @Override
    public Element toXml(Document document) {
        Element optionsElem = DomUtil.createElement(document, "options", DeltaVConstants.NAMESPACE);
        for (String localName : this.entriesLocalNames) {
            DomUtil.addChildElement(optionsElem, localName, DeltaVConstants.NAMESPACE);
        }
        return optionsElem;
    }

    public static OptionsInfo createFromXml(Element optionsElement) throws DavException {
        if (!DomUtil.matches(optionsElement, "options", DeltaVConstants.NAMESPACE)) {
            log.warn("DAV:options element expected");
            throw new DavException(400);
        }
        OptionsInfo oInfo = new OptionsInfo();
        ElementIterator it = DomUtil.getChildren(optionsElement);
        while (it.hasNext()) {
            oInfo.entriesLocalNames.add(it.nextElement().getLocalName());
        }
        return oInfo;
    }
}

