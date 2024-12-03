/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.property;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NamespacesProperty
extends AbstractDavProperty<Map<String, String>>
implements ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(NamespacesProperty.class);
    private final Map<String, String> value = new HashMap<String, String>();

    public NamespacesProperty(NamespaceRegistry nsReg) throws RepositoryException {
        super(JCR_NAMESPACES, false);
        if (nsReg != null) {
            for (String prefix : nsReg.getPrefixes()) {
                this.value.put(prefix, nsReg.getURI(prefix));
            }
        }
    }

    public NamespacesProperty(Map<String, String> namespaces) {
        super(JCR_NAMESPACES, false);
        this.value.putAll(namespaces);
    }

    public NamespacesProperty(DavProperty<?> property) throws DavException {
        super(JCR_NAMESPACES, false);
        Object v = property.getValue();
        if (!(v instanceof List)) {
            log.warn("Unexpected structure of dcr:namespace property.");
            throw new DavException(500);
        }
        for (Object listEntry : (List)v) {
            Element e;
            if (!(listEntry instanceof Element) || !"namespace".equals((e = (Element)listEntry).getLocalName())) continue;
            Element pElem = DomUtil.getChildElement(e, "prefix", ItemResourceConstants.NAMESPACE);
            String prefix = DomUtil.getText(pElem, Namespace.EMPTY_NAMESPACE.getPrefix());
            Element uElem = DomUtil.getChildElement(e, "uri", ItemResourceConstants.NAMESPACE);
            String uri = DomUtil.getText(uElem, Namespace.EMPTY_NAMESPACE.getURI());
            this.value.put(prefix, uri);
        }
    }

    public Map<String, String> getNamespaces() {
        return Collections.unmodifiableMap(this.value);
    }

    @Override
    public Map<String, String> getValue() {
        return Collections.unmodifiableMap(this.value);
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (String prefix : this.value.keySet()) {
            String uri = this.value.get(prefix);
            Element nsElem = DomUtil.addChildElement(elem, "namespace", ItemResourceConstants.NAMESPACE);
            DomUtil.addChildElement(nsElem, "prefix", ItemResourceConstants.NAMESPACE, prefix);
            DomUtil.addChildElement(nsElem, "uri", ItemResourceConstants.NAMESPACE, uri);
        }
        return elem;
    }
}

