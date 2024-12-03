/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.util.XMLizable;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.merge.PropertyMerger;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Metadata
implements XMLizable,
PropertyAccess {
    private Map<QName, XMPProperty> properties = new HashMap<QName, XMPProperty>();

    @Override
    public void setProperty(XMPProperty prop) {
        this.properties.put(prop.getName(), prop);
    }

    @Override
    public XMPProperty getProperty(String uri, String localName) {
        return this.getProperty(new QName(uri, localName));
    }

    @Override
    public XMPProperty getProperty(QName name) {
        return this.properties.get(name);
    }

    @Override
    public XMPProperty removeProperty(QName name) {
        return this.properties.remove(name);
    }

    @Override
    public XMPProperty getValueProperty() {
        return this.getProperty(XMPConstants.RDF_VALUE);
    }

    @Override
    public int getPropertyCount() {
        return this.properties.size();
    }

    @Override
    public Iterator iterator() {
        return this.properties.keySet().iterator();
    }

    public void mergeInto(Metadata target, List<Class> exclude) {
        XMPSchemaRegistry registry = XMPSchemaRegistry.getInstance();
        for (XMPProperty o : this.properties.values()) {
            XMPProperty prop = o;
            XMPSchema schema = registry.getSchema(prop.getNamespace());
            if (exclude.contains(schema.getClass())) continue;
            MergeRuleSet rules = schema.getDefaultMergeRuleSet();
            PropertyMerger merger = rules.getPropertyMergerFor(prop);
            merger.merge(prop, target);
        }
    }

    @Override
    public void toSAX(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        handler.startPrefixMapping("x", "adobe:ns:meta/");
        handler.startElement("adobe:ns:meta/", "xmpmeta", "x:xmpmeta", atts);
        handler.startPrefixMapping("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:RDF", atts);
        this.writeCustomDescription(handler);
        HashSet<String> namespaces = new HashSet<String>();
        for (QName n : this.properties.keySet()) {
            namespaces.add(n.getNamespaceURI());
        }
        for (String ns : namespaces) {
            XMPSchema schema = XMPSchemaRegistry.getInstance().getSchema(ns);
            String prefix = schema != null ? schema.getPreferredPrefix() : null;
            boolean first = true;
            boolean empty = true;
            for (XMPProperty o : this.properties.values()) {
                XMPProperty prop = o;
                if (!prop.getName().getNamespaceURI().equals(ns) || prop.attribute) continue;
                if (first) {
                    if (prefix == null) {
                        prefix = prop.getName().getPrefix();
                    }
                    atts.clear();
                    atts.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about", "rdf:about", "CDATA", "");
                    if (prefix != null) {
                        handler.startPrefixMapping(prefix, ns);
                    }
                    handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf:Description", atts);
                    empty = false;
                    first = false;
                }
                prop.toSAX(handler);
            }
            if (empty) continue;
            handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf:Description");
            if (prefix == null) continue;
            handler.endPrefixMapping(prefix);
        }
        handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:RDF");
        handler.endPrefixMapping("rdf");
        handler.endElement("adobe:ns:meta/", "xmpmeta", "x:xmpmeta");
        handler.endPrefixMapping("x");
    }

    private void writeCustomDescription(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        boolean empty = true;
        for (XMPProperty prop : this.properties.values()) {
            if (!prop.attribute) continue;
            atts.addAttribute(prop.getNamespace(), prop.getName().getLocalName(), prop.getName().getQName(), "CDATA", (String)prop.getValue());
            if (prop.getName().getPrefix() != null) {
                handler.startPrefixMapping(prop.getName().getPrefix(), prop.getNamespace());
                handler.endPrefixMapping(prop.getName().getPrefix());
            }
            empty = false;
        }
        if (!empty) {
            atts.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about", "rdf:about", "CDATA", "");
            handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:Description", atts);
            handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:Description");
        }
    }
}

