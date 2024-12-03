/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.ContainerElement;
import com.sun.xml.txw2.Document;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.annotation.XmlNamespace;
import com.sun.xml.txw2.output.TXWSerializer;
import com.sun.xml.txw2.output.XmlSerializer;
import javax.xml.namespace.QName;

public abstract class TXW {
    private TXW() {
    }

    static QName getTagName(Class<?> c) {
        XmlNamespace xn;
        Package pkg;
        String localName = "";
        String nsUri = "##default";
        XmlElement xe = c.getAnnotation(XmlElement.class);
        if (xe != null) {
            localName = xe.value();
            nsUri = xe.ns();
        }
        if (localName.length() == 0) {
            localName = c.getName();
            int idx = localName.lastIndexOf(46);
            if (idx >= 0) {
                localName = localName.substring(idx + 1);
            }
            localName = Character.toLowerCase(localName.charAt(0)) + localName.substring(1);
        }
        if (nsUri.equals("##default") && (pkg = c.getPackage()) != null && (xn = pkg.getAnnotation(XmlNamespace.class)) != null) {
            nsUri = xn.value();
        }
        if (nsUri.equals("##default")) {
            nsUri = "";
        }
        return new QName(nsUri, localName);
    }

    public static <T extends TypedXmlWriter> T create(Class<T> rootElement, XmlSerializer out) {
        if (out instanceof TXWSerializer) {
            TXWSerializer txws = (TXWSerializer)out;
            return txws.txw._element(rootElement);
        }
        Document doc = new Document(out);
        QName n = TXW.getTagName(rootElement);
        return new ContainerElement(doc, null, n.getNamespaceURI(), n.getLocalPart())._cast(rootElement);
    }

    public static <T extends TypedXmlWriter> T create(QName tagName, Class<T> rootElement, XmlSerializer out) {
        if (out instanceof TXWSerializer) {
            TXWSerializer txws = (TXWSerializer)out;
            return txws.txw._element(tagName, rootElement);
        }
        return new ContainerElement(new Document(out), null, tagName.getNamespaceURI(), tagName.getLocalPart())._cast(rootElement);
    }
}

