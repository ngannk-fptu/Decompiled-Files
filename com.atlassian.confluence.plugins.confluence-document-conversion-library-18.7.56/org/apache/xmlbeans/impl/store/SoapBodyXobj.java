/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPBody;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Document;

class SoapBodyXobj
extends SoapElementXobj
implements SOAPBody {
    SoapBodyXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapBodyXobj(l, this._name);
    }

    @Override
    public boolean hasFault() {
        return DomImpl.soapBody_hasFault(this);
    }

    @Override
    public SOAPFault addFault() throws SOAPException {
        return DomImpl.soapBody_addFault(this);
    }

    @Override
    public SOAPFault getFault() {
        return DomImpl.soapBody_getFault(this);
    }

    @Override
    public SOAPBodyElement addBodyElement(Name name) {
        return DomImpl.soapBody_addBodyElement(this, name);
    }

    @Override
    public SOAPBodyElement addDocument(Document document) {
        return DomImpl.soapBody_addDocument(this, document);
    }

    @Override
    public SOAPFault addFault(Name name, String s) throws SOAPException {
        return DomImpl.soapBody_addFault(this, name, s);
    }

    @Override
    public SOAPFault addFault(Name faultCode, String faultString, java.util.Locale locale) throws SOAPException {
        return DomImpl.soapBody_addFault(this, faultCode, faultString, locale);
    }
}

