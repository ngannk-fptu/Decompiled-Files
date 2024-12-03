/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPComplexValue;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMPStructure
extends XMPComplexValue
implements PropertyAccess {
    protected Map properties = new HashMap();

    @Override
    public Object getSimpleValue() {
        return null;
    }

    @Override
    public void setProperty(XMPProperty prop) {
        this.properties.put(prop.getName(), prop);
    }

    @Override
    public XMPProperty getProperty(String uri, String localName) {
        return this.getProperty(new QName(uri, localName));
    }

    @Override
    public XMPProperty getValueProperty() {
        return this.getProperty(XMPConstants.RDF_VALUE);
    }

    @Override
    public XMPProperty getProperty(QName name) {
        XMPProperty prop = (XMPProperty)this.properties.get(name);
        return prop;
    }

    @Override
    public XMPProperty removeProperty(QName name) {
        return (XMPProperty)this.properties.remove(name);
    }

    @Override
    public int getPropertyCount() {
        return this.properties.size();
    }

    @Override
    public Iterator iterator() {
        return this.properties.keySet().iterator();
    }

    @Override
    public void toSAX(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.clear();
        handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:Description", atts);
        for (Object o : this.properties.values()) {
            XMPProperty prop = (XMPProperty)o;
            prop.toSAX(handler);
        }
        handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:Description");
    }

    public String toString() {
        return "XMP structure: " + this.getPropertyCount();
    }
}

