/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OptionsResponse
implements DeltaVConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(OptionsResponse.class);
    private final Map<String, Entry> entries = new HashMap<String, Entry>();

    public void addEntry(String localName, Namespace namespace, String[] hrefs) {
        Entry entry = new Entry(localName, namespace, hrefs);
        this.entries.put(DomUtil.getExpandedName(localName, namespace), entry);
    }

    public String[] getHrefs(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (this.entries.containsKey(key)) {
            return this.entries.get(key).hrefs;
        }
        return new String[0];
    }

    @Override
    public Element toXml(Document document) {
        Element optionsResponse = DomUtil.createElement(document, "options-response", NAMESPACE);
        for (Entry entry : this.entries.values()) {
            Element elem = DomUtil.addChildElement(optionsResponse, entry.localName, entry.namespace);
            for (String href : entry.hrefs) {
                elem.appendChild(DomUtil.hrefToXml(href, document));
            }
        }
        return optionsResponse;
    }

    public static OptionsResponse createFromXml(Element orElem) {
        if (!DomUtil.matches(orElem, "options-response", NAMESPACE)) {
            throw new IllegalArgumentException("DAV:options-response element expected");
        }
        OptionsResponse oResponse = new OptionsResponse();
        ElementIterator it = DomUtil.getChildren(orElem);
        while (it.hasNext()) {
            Element el = it.nextElement();
            ArrayList<String> hrefs = new ArrayList<String>();
            ElementIterator hrefIt = DomUtil.getChildren(el, "href", DavConstants.NAMESPACE);
            while (hrefIt.hasNext()) {
                hrefs.add(DomUtil.getTextTrim(hrefIt.nextElement()));
            }
            oResponse.addEntry(el.getLocalName(), DomUtil.getNamespace(el), hrefs.toArray(new String[hrefs.size()]));
        }
        return oResponse;
    }

    private static class Entry {
        private final String localName;
        private final Namespace namespace;
        private final String[] hrefs;

        private Entry(String localName, Namespace namespace, String[] hrefs) {
            this.localName = localName;
            this.namespace = namespace;
            this.hrefs = hrefs;
        }
    }
}

