/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.SOAPBodyElement;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapBodyElementXobj
extends SoapElementXobj
implements SOAPBodyElement {
    SoapBodyElementXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapBodyElementXobj(l, this._name);
    }
}

