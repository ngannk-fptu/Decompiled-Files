/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.StringWriter;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Div;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDiv
extends FOMExtensibleElement
implements Div {
    private static final long serialVersionUID = -2319449893405850433L;

    protected FOMDiv(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMDiv(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMDiv(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    public String[] getXhtmlClass() {
        String _class = this.getAttributeValue(CLASS);
        String[] classes = null;
        if (_class != null) {
            classes = _class.split(" ");
        }
        return classes;
    }

    public String getId() {
        return this.getAttributeValue(AID);
    }

    public String getTitle() {
        return this.getAttributeValue(ATITLE);
    }

    public Div setId(String id) {
        this.complete();
        if (id != null) {
            this.setAttributeValue(AID, id);
        } else {
            this.removeAttribute(AID);
        }
        return this;
    }

    public Div setTitle(String title) {
        this.complete();
        if (title != null) {
            this.setAttributeValue(ATITLE, title);
        } else {
            this.removeAttribute(ATITLE);
        }
        return this;
    }

    public Div setXhtmlClass(String[] classes) {
        this.complete();
        if (classes != null) {
            StringBuilder val = new StringBuilder();
            for (String s : classes) {
                if (s.length() > 0) {
                    val.append(" ");
                }
                val.append(s);
            }
            this.setAttributeValue(CLASS, val.toString());
        } else {
            this.removeAttribute(CLASS);
        }
        return this;
    }

    public String getValue() {
        return this.getInternalValue();
    }

    public void setValue(String value) {
        this.complete();
        this._removeAllChildren();
        if (value != null) {
            IRI baseUri = null;
            value = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + value + "</div>";
            OMContainer element = null;
            try {
                baseUri = this.getResolvedBaseUri();
                element = (OMElement)((Object)this._parse(value, baseUri));
            }
            catch (Exception e) {
                // empty catch block
            }
            Iterator i = element.getChildren();
            while (i.hasNext()) {
                this.addChild((OMNode)i.next());
            }
        }
    }

    protected String getInternalValue() {
        try {
            StringWriter out = new StringWriter();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            writer.writeStartElement("");
            Iterator nodes = this.getChildren();
            while (nodes.hasNext()) {
                OMNode node = (OMNode)nodes.next();
                node.serialize(writer);
            }
            writer.writeEndElement();
            return out.getBuffer().toString().substring(2);
        }
        catch (Exception exception) {
            return "";
        }
    }
}

