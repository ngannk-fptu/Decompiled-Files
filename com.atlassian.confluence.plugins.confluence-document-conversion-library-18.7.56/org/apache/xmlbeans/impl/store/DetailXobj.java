/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.Detail;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapFaultElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class DetailXobj
extends SoapFaultElementXobj
implements Detail {
    DetailXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    Xobj newNode(Locale l) {
        return new DetailXobj(l, this._name);
    }

    @Override
    public DetailEntry addDetailEntry(Name name) {
        return DomImpl.detail_addDetailEntry(this, name);
    }

    @Override
    public Iterator<DetailEntry> getDetailEntries() {
        return DomImpl.detail_getDetailEntries(this);
    }
}

