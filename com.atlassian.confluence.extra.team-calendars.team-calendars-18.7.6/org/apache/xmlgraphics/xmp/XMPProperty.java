/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.util.XMLizable;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPArray;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPComplexValue;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;
import org.apache.xmlgraphics.xmp.XMPStructure;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMPProperty
implements XMLizable {
    private QName name;
    private Object value;
    private String xmllang;
    private Map qualifiers;
    protected boolean attribute;

    public XMPProperty(QName name, Object value) {
        this.name = name;
        this.value = value;
    }

    public QName getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.getName().getNamespaceURI();
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public void setXMLLang(String lang) {
        this.xmllang = lang;
    }

    public String getXMLLang() {
        return this.xmllang;
    }

    public boolean isArray() {
        return this.value instanceof XMPArray;
    }

    public XMPArray getArrayValue() {
        return this.isArray() ? (XMPArray)this.value : null;
    }

    public XMPArray convertSimpleValueToArray(XMPArrayType type) {
        if (this.getArrayValue() == null) {
            XMPArray array = new XMPArray(type);
            if (this.getXMLLang() != null) {
                array.add(this.getValue().toString(), this.getXMLLang());
            } else {
                array.add(this.getValue());
            }
            this.setValue(array);
            this.setXMLLang(null);
            return array;
        }
        return this.getArrayValue();
    }

    public PropertyAccess getStructureValue() {
        return this.value instanceof XMPStructure ? (XMPStructure)this.value : null;
    }

    private boolean hasPropertyQualifiers() {
        return this.qualifiers == null || this.qualifiers.size() == 0;
    }

    public boolean isQualifiedProperty() {
        PropertyAccess props = this.getStructureValue();
        if (props != null) {
            XMPProperty rdfValue = props.getValueProperty();
            return rdfValue != null;
        }
        return this.hasPropertyQualifiers();
    }

    public void simplify() {
        XMPProperty rdfValue;
        PropertyAccess props = this.getStructureValue();
        if (props != null && (rdfValue = props.getValueProperty()) != null) {
            if (this.hasPropertyQualifiers()) {
                throw new IllegalStateException("Illegal internal state (qualifiers present on non-simplified property)");
            }
            XMPProperty prop = new XMPProperty(this.getName(), rdfValue);
            Iterator iter = props.iterator();
            while (iter.hasNext()) {
                QName name = (QName)iter.next();
                if (XMPConstants.RDF_VALUE.equals(name)) continue;
                prop.setPropertyQualifier(name, props.getProperty(name));
            }
            props.setProperty(prop);
        }
    }

    private void setPropertyQualifier(QName name, XMPProperty property) {
        if (this.qualifiers == null) {
            this.qualifiers = new HashMap();
        }
        this.qualifiers.put(name, property);
    }

    private String getEffectiveQName() {
        String prefix = this.getName().getPrefix();
        if (prefix == null || "".equals(prefix)) {
            XMPSchema schema = XMPSchemaRegistry.getInstance().getSchema(this.getNamespace());
            if (schema == null) {
                return this.getName().getLocalName();
            }
            prefix = schema.getPreferredPrefix();
        }
        return prefix + ":" + this.getName().getLocalName();
    }

    @Override
    public void toSAX(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        String qName = this.getEffectiveQName();
        if (this.value instanceof URI) {
            atts.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource", "rdf:resource", "CDATA", ((URI)this.value).toString());
        }
        handler.startElement(this.getName().getNamespaceURI(), this.getName().getLocalName(), qName, atts);
        if (this.value instanceof XMPComplexValue) {
            XMPComplexValue cv = (XMPComplexValue)this.value;
            cv.toSAX(handler);
        } else if (!(this.value instanceof URI)) {
            char[] chars = this.value.toString().toCharArray();
            handler.characters(chars, 0, chars.length);
        }
        handler.endElement(this.getName().getNamespaceURI(), this.getName().getLocalName(), qName);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("XMP Property ");
        sb.append(this.getName()).append(": ");
        sb.append(this.getValue());
        return sb.toString();
    }
}

