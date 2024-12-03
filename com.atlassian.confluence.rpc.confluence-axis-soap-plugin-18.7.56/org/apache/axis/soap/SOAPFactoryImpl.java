/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.apache.axis.message.Detail;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;

public class SOAPFactoryImpl
extends SOAPFactory {
    public SOAPElement createElement(Name name) throws SOAPException {
        return new MessageElement(name);
    }

    public SOAPElement createElement(String localName) throws SOAPException {
        return new MessageElement("", localName);
    }

    public SOAPElement createElement(String localName, String prefix, String uri) throws SOAPException {
        return new MessageElement(localName, prefix, uri);
    }

    public javax.xml.soap.Detail createDetail() throws SOAPException {
        return new Detail();
    }

    public Name createName(String localName, String prefix, String uri) throws SOAPException {
        return new PrefixedQName(uri, localName, prefix);
    }

    public Name createName(String localName) throws SOAPException {
        return new PrefixedQName("", localName, "");
    }
}

