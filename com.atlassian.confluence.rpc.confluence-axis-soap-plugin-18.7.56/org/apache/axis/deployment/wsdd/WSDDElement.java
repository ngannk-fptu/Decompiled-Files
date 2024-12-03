/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class WSDDElement
extends WSDDConstants
implements Serializable {
    private String name;

    public WSDDElement() {
    }

    public WSDDElement(Element e) throws WSDDException {
        this.validateCandidateElement(e);
    }

    protected abstract QName getElementName();

    private void validateCandidateElement(Element e) throws WSDDException {
        QName name = this.getElementName();
        if (null == e || null == e.getNamespaceURI() || null == e.getLocalName() || !e.getNamespaceURI().equals(name.getNamespaceURI()) || !e.getLocalName().equals(name.getLocalPart())) {
            throw new WSDDException(Messages.getMessage("invalidWSDD00", e.getLocalName(), name.getLocalPart()));
        }
    }

    public Element getChildElement(Element e, String name) {
        Element[] elements = this.getChildElements(e, name);
        if (elements.length == 0) {
            return null;
        }
        return elements[0];
    }

    public Element[] getChildElements(Element e, String name) {
        NodeList nl = e.getChildNodes();
        Vector<Element> els = new Vector<Element>();
        for (int i = 0; i < nl.getLength(); ++i) {
            Element el;
            Node thisNode = nl.item(i);
            if (!(thisNode instanceof Element) || !(el = (Element)thisNode).getLocalName().equals(name)) continue;
            els.add(el);
        }
        Element[] elements = new Element[els.size()];
        els.toArray(elements);
        return elements;
    }

    public abstract void writeToContext(SerializationContext var1) throws IOException;
}

