/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Detail;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPFault;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapBodyElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapFaultXobj
extends SoapBodyElementXobj
implements SOAPFault {
    SoapFaultXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapFaultXobj(l, this._name);
    }

    @Override
    public void setFaultString(String faultString) {
        DomImpl.soapFault_setFaultString(this, faultString);
    }

    @Override
    public void setFaultString(String faultString, java.util.Locale locale) {
        DomImpl.soapFault_setFaultString(this, faultString, locale);
    }

    @Override
    public void setFaultCode(Name faultCodeName) throws SOAPException {
        DomImpl.soapFault_setFaultCode((DomImpl.Dom)this, faultCodeName);
    }

    @Override
    public void setFaultActor(String faultActorString) {
        DomImpl.soapFault_setFaultActor(this, faultActorString);
    }

    @Override
    public String getFaultActor() {
        return DomImpl.soapFault_getFaultActor(this);
    }

    @Override
    public String getFaultCode() {
        return DomImpl.soapFault_getFaultCode(this);
    }

    @Override
    public void setFaultCode(String faultCode) throws SOAPException {
        DomImpl.soapFault_setFaultCode((DomImpl.Dom)this, faultCode);
    }

    @Override
    public java.util.Locale getFaultStringLocale() {
        return DomImpl.soapFault_getFaultStringLocale(this);
    }

    @Override
    public Name getFaultCodeAsName() {
        return DomImpl.soapFault_getFaultCodeAsName(this);
    }

    @Override
    public String getFaultString() {
        return DomImpl.soapFault_getFaultString(this);
    }

    @Override
    public Detail addDetail() throws SOAPException {
        return DomImpl.soapFault_addDetail(this);
    }

    @Override
    public Detail getDetail() {
        return DomImpl.soapFault_getDetail(this);
    }
}

