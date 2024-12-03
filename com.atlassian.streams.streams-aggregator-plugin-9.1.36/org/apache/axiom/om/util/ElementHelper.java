/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.util.stax.xop.XOPUtils;

public class ElementHelper {
    private OMElement element;

    public ElementHelper(OMElement element) {
        this.element = element;
    }

    public QName resolveQName(String qname, boolean defaultToParentNameSpace) {
        int colon = qname.indexOf(58);
        if (colon < 0) {
            OMNamespace namespace;
            if (defaultToParentNameSpace && (namespace = this.element.getNamespace()) != null) {
                if (namespace.getPrefix() == null) {
                    return new QName(namespace.getNamespaceURI(), qname);
                }
                return new QName(namespace.getNamespaceURI(), qname, namespace.getPrefix());
            }
            return new QName(qname);
        }
        String prefix = qname.substring(0, colon);
        String local = qname.substring(colon + 1);
        if (local.length() == 0) {
            return null;
        }
        OMNamespace namespace = this.element.findNamespaceURI(prefix);
        if (namespace == null) {
            return null;
        }
        return new QName(namespace.getNamespaceURI(), local, prefix);
    }

    public QName resolveQName(String qname) {
        return this.resolveQName(qname, true);
    }

    public static void setNewElement(OMElement parent, OMElement myElement, OMElement newElement) {
        if (myElement != null) {
            myElement.discard();
        }
        parent.addChild(newElement);
    }

    public static OMElement getChildWithName(OMElement parent, String childName) {
        Iterator childrenIter = parent.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode)childrenIter.next();
            if (node.getType() != 1 || !childName.equals(((OMElement)node).getLocalName())) continue;
            return (OMElement)node;
        }
        return null;
    }

    public static String getContentID(XMLStreamReader parser, String charsetEncoding) {
        return ElementHelper.getContentID(parser);
    }

    public static String getContentID(XMLStreamReader parser) {
        if (parser.getAttributeCount() > 0 && parser.getAttributeLocalName(0).equals("href")) {
            return ElementHelper.getContentIDFromHref(parser.getAttributeValue(0));
        }
        throw new OMException("Href attribute not found in XOP:Include element");
    }

    public static String getContentIDFromHref(String href) {
        return XOPUtils.getContentIDFromURL(href);
    }

    public static OMElement importOMElement(OMElement omElement, OMFactory omFactory) {
        if (omElement.getOMFactory().getClass().isInstance(omFactory)) {
            return omElement;
        }
        OMElement documentElement = omFactory.getMetaFactory().createStAXOMBuilder(omFactory, omElement.getXMLStreamReader()).getDocumentElement();
        documentElement.build();
        return documentElement;
    }

    public static SOAPHeaderBlock toSOAPHeaderBlock(OMElement omElement, SOAPFactory factory) throws Exception {
        if (omElement instanceof SOAPHeaderBlock) {
            return (SOAPHeaderBlock)omElement;
        }
        QName name = omElement.getQName();
        String localName = name.getLocalPart();
        OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        omElement.serialize(baos);
        ByteArrayDataSource bads = new ByteArrayDataSource(baos.toByteArray(), "utf-8");
        SOAPHeaderBlock block = factory.createSOAPHeaderBlock(localName, namespace, bads);
        return block;
    }

    public static Reader getTextAsStream(OMElement element, boolean cache) {
        return element.getTextAsStream(cache);
    }

    public static void writeTextTo(OMElement element, Writer out, boolean cache) throws XMLStreamException, IOException {
        element.writeTextTo(out, cache);
    }
}

