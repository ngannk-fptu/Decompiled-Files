/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.DocumentFragment;

class DocumentFragXobj
extends NodeXobj
implements DocumentFragment {
    DocumentFragXobj(Locale l) {
        super(l, 1, 11);
    }

    @Override
    Xobj newNode(Locale l) {
        return new DocumentFragXobj(l);
    }
}

