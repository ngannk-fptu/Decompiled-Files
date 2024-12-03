/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.SOAPFaultElement;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapFaultElementXobj
extends SoapElementXobj
implements SOAPFaultElement {
    SoapFaultElementXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapFaultElementXobj(l, this._name);
    }
}

