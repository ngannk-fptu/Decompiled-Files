/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;

abstract class NamedNodeXobj
extends NodeXobj {
    boolean _canHavePrefixUri = true;

    NamedNodeXobj(Locale l, int kind, int domType) {
        super(l, kind, domType);
    }

    @Override
    public boolean nodeCanHavePrefixUri() {
        return this._canHavePrefixUri;
    }
}

