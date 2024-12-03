/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPElement
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.messaging.saaj.soap.impl.TreeException;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Body1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Envelope1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.FaultElement1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.Header1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Body1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.Header1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import java.util.Objects;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.Element;

public class ElementFactory {
    public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, Name name) {
        return ElementFactory.createElement(ownerDocument, name.getLocalName(), name.getPrefix(), name.getURI());
    }

    public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, QName name) {
        return ElementFactory.createElement(ownerDocument, name.getLocalPart(), name.getPrefix(), name.getNamespaceURI());
    }

    public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, Element element) {
        Objects.requireNonNull(ownerDocument);
        Objects.requireNonNull(element);
        String localName = element.getLocalName();
        String namespaceUri = element.getNamespaceURI();
        String prefix = element.getPrefix();
        if ("Envelope".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Envelope1_1Impl(ownerDocument, element);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Envelope1_2Impl(ownerDocument, element);
            }
        }
        if ("Body".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Body1_1Impl(ownerDocument, element);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Body1_2Impl(ownerDocument, element);
            }
        }
        if ("Header".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Header1_1Impl(ownerDocument, element);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Header1_2Impl(ownerDocument, element);
            }
        }
        if ("Fault".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Fault1_1Impl(ownerDocument, element);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Fault1_2Impl(ownerDocument, element);
            }
        }
        if ("Detail".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Detail1_1Impl(ownerDocument, element);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Detail1_2Impl(ownerDocument, element);
            }
        }
        if (("faultcode".equalsIgnoreCase(localName) || "faultstring".equalsIgnoreCase(localName) || "faultactor".equalsIgnoreCase(localName)) && "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new FaultElement1_1Impl(ownerDocument, localName, prefix);
        }
        return new ElementImpl(ownerDocument, element);
    }

    public static SOAPElement createElement(SOAPDocumentImpl ownerDocument, String localName, String prefix, String namespaceUri) {
        SOAPElement newElement;
        if (ownerDocument == null) {
            ownerDocument = "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri) ? new SOAPPart1_1Impl().getDocument() : ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri) ? new SOAPPart1_2Impl().getDocument() : new SOAPDocumentImpl(null));
        }
        return (newElement = ElementFactory.createNamedElement(ownerDocument, localName, prefix, namespaceUri)) != null ? newElement : new ElementImpl(ownerDocument, namespaceUri, NameImpl.createQName(prefix, localName));
    }

    public static SOAPElement createNamedElement(SOAPDocumentImpl ownerDocument, String localName, String prefix, String namespaceUri) {
        if (prefix == null) {
            prefix = "SOAP-ENV";
        }
        if ("Envelope".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Envelope1_1Impl(ownerDocument, prefix);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Envelope1_2Impl(ownerDocument, prefix);
            }
        }
        if ("Body".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Body1_1Impl(ownerDocument, prefix);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Body1_2Impl(ownerDocument, prefix);
            }
        }
        if ("Header".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Header1_1Impl(ownerDocument, prefix);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Header1_2Impl(ownerDocument, prefix);
            }
        }
        if ("Fault".equalsIgnoreCase(localName)) {
            FaultImpl fault = null;
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                fault = new Fault1_1Impl(ownerDocument, prefix);
            } else if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                fault = new Fault1_2Impl(ownerDocument, prefix);
            }
            if (fault != null) {
                return fault;
            }
        }
        if ("Detail".equalsIgnoreCase(localName)) {
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
                return new Detail1_1Impl(ownerDocument, prefix);
            }
            if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceUri)) {
                return new Detail1_2Impl(ownerDocument, prefix);
            }
        }
        if (("faultcode".equalsIgnoreCase(localName) || "faultstring".equalsIgnoreCase(localName) || "faultactor".equalsIgnoreCase(localName)) && "http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceUri)) {
            return new FaultElement1_1Impl(ownerDocument, localName, prefix);
        }
        return null;
    }

    protected static void invalidCreate(String msg) {
        throw new TreeException(msg);
    }
}

