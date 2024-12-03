/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapEnvelopeXobj
extends SoapElementXobj
implements SOAPEnvelope {
    SoapEnvelopeXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapEnvelopeXobj(l, this._name);
    }

    @Override
    public SOAPBody addBody() throws SOAPException {
        return DomImpl._soapEnvelope_addBody(this);
    }

    @Override
    public SOAPBody getBody() throws SOAPException {
        return DomImpl._soapEnvelope_getBody(this);
    }

    @Override
    public SOAPHeader getHeader() throws SOAPException {
        return DomImpl._soapEnvelope_getHeader(this);
    }

    @Override
    public SOAPHeader addHeader() throws SOAPException {
        return DomImpl._soapEnvelope_addHeader(this);
    }

    @Override
    public Name createName(String localName) {
        return DomImpl._soapEnvelope_createName(this, localName);
    }

    @Override
    public Name createName(String localName, String prefix, String namespaceURI) {
        return DomImpl._soapEnvelope_createName(this, localName, prefix, namespaceURI);
    }
}

