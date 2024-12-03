/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.store.AttrXobj;
import org.apache.xmlbeans.impl.store.Locale;

class AttrIdXobj
extends AttrXobj {
    AttrIdXobj(Locale l, QName name) {
        super(l, name);
    }

    @Override
    public boolean isId() {
        return true;
    }
}

