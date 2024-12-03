/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.Collections;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ElementMappingContentHandler
extends ContentHandlerDecorator {
    private final Map<QName, TargetElement> mappings;

    public ElementMappingContentHandler(ContentHandler handler, Map<QName, TargetElement> mappings) {
        super(handler);
        this.mappings = mappings;
    }

    protected static final String getQNameAsString(QName qname) {
        String prefix = qname.getPrefix();
        if (prefix.length() > 0) {
            return prefix + ":" + qname.getLocalPart();
        }
        return qname.getLocalPart();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        TargetElement mapping = this.mappings.get(new QName(namespaceURI, localName));
        if (mapping != null) {
            QName tag = mapping.getMappedTagName();
            super.startElement(tag.getNamespaceURI(), tag.getLocalPart(), ElementMappingContentHandler.getQNameAsString(tag), mapping.mapAttributes(atts));
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        TargetElement mapping = this.mappings.get(new QName(namespaceURI, localName));
        if (mapping != null) {
            QName tag = mapping.getMappedTagName();
            super.endElement(tag.getNamespaceURI(), tag.getLocalPart(), ElementMappingContentHandler.getQNameAsString(tag));
        }
    }

    public static class TargetElement {
        private final QName mappedTagName;
        private final Map<QName, QName> attributesMapping;

        public TargetElement(QName mappedTagName, Map<QName, QName> attributesMapping) {
            this.mappedTagName = mappedTagName;
            this.attributesMapping = attributesMapping;
        }

        public TargetElement(String mappedTagURI, String mappedTagLocalName, Map<QName, QName> attributesMapping) {
            this(new QName(mappedTagURI, mappedTagLocalName), attributesMapping);
        }

        public TargetElement(QName mappedTagName) {
            this(mappedTagName, Collections.emptyMap());
        }

        public TargetElement(String mappedTagURI, String mappedTagLocalName) {
            this(mappedTagURI, mappedTagLocalName, Collections.emptyMap());
        }

        public QName getMappedTagName() {
            return this.mappedTagName;
        }

        public Map<QName, QName> getAttributesMapping() {
            return this.attributesMapping;
        }

        public Attributes mapAttributes(Attributes atts) {
            AttributesImpl natts = new AttributesImpl();
            for (int i = 0; i < atts.getLength(); ++i) {
                QName name = this.attributesMapping.get(new QName(atts.getURI(i), atts.getLocalName(i)));
                if (name == null) continue;
                natts.addAttribute(name.getNamespaceURI(), name.getLocalPart(), ElementMappingContentHandler.getQNameAsString(name), atts.getType(i), atts.getValue(i));
            }
            return natts;
        }
    }
}

