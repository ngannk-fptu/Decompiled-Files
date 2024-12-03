/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.DocumentXobj;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapPartDom;
import org.apache.xmlbeans.impl.store.Xobj;

class SoapPartDocXobj
extends DocumentXobj {
    SoapPartDom _soapPartDom = new SoapPartDom(this);

    SoapPartDocXobj(Locale l) {
        super(l);
    }

    @Override
    DomImpl.Dom getDom() {
        return this._soapPartDom;
    }

    @Override
    Xobj newNode(Locale l) {
        return new SoapPartDocXobj(l);
    }
}

