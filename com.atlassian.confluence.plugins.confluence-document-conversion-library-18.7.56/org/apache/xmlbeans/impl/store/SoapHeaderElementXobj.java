/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapHeaderElementXobj
extends SoapElementXobj
implements SOAPHeaderElement {
    SoapHeaderElementXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapHeaderElementXobj(l, this._name);
    }

    @Override
    public void setMustUnderstand(boolean mustUnderstand) {
        DomImpl.soapHeaderElement_setMustUnderstand(this, mustUnderstand);
    }

    @Override
    public boolean getMustUnderstand() {
        return DomImpl.soapHeaderElement_getMustUnderstand(this);
    }

    @Override
    public void setActor(String actor) {
        DomImpl.soapHeaderElement_setActor(this, actor);
    }

    @Override
    public String getActor() {
        return DomImpl.soapHeaderElement_getActor(this);
    }
}

