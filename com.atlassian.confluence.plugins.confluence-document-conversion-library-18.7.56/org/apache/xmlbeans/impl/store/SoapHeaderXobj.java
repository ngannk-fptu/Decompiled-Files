/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPHeader;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapHeaderXobj
extends SoapElementXobj
implements SOAPHeader {
    SoapHeaderXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapHeaderXobj(l, this._name);
    }

    @Override
    public Iterator<SOAPHeaderElement> examineAllHeaderElements() {
        return DomImpl.soapHeader_examineAllHeaderElements(this);
    }

    @Override
    public Iterator<SOAPHeaderElement> extractAllHeaderElements() {
        return DomImpl.soapHeader_extractAllHeaderElements(this);
    }

    @Override
    public Iterator<SOAPHeaderElement> examineHeaderElements(String actor) {
        return DomImpl.soapHeader_examineHeaderElements(this, actor);
    }

    @Override
    public Iterator<SOAPHeaderElement> examineMustUnderstandHeaderElements(String mustUnderstandString) {
        return DomImpl.soapHeader_examineMustUnderstandHeaderElements(this, mustUnderstandString);
    }

    @Override
    public Iterator<SOAPHeaderElement> extractHeaderElements(String actor) {
        return DomImpl.soapHeader_extractHeaderElements(this, actor);
    }

    @Override
    public SOAPHeaderElement addHeaderElement(Name name) {
        return DomImpl.soapHeader_addHeaderElement(this, name);
    }
}

