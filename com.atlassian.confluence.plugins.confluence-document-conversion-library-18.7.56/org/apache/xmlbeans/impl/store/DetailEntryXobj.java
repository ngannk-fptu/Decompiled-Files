/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapElementXobj;
import org.apache.xmlbeans.impl.store.Xobj;

class DetailEntryXobj
extends SoapElementXobj
implements DetailEntry {
    @Override
    Xobj newNode(Locale l) {
        return new DetailEntryXobj(l, this._name);
    }

    DetailEntryXobj(Locale l, QName name) {
        super(l, name);
    }
}

