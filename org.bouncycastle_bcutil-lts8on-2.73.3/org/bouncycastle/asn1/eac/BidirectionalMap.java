/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import java.util.Hashtable;

public class BidirectionalMap
extends Hashtable {
    private static final long serialVersionUID = -7457289971962812909L;
    Hashtable reverseMap = new Hashtable();

    public Object getReverse(Object o) {
        return this.reverseMap.get(o);
    }

    @Override
    public Object put(Object key, Object o) {
        this.reverseMap.put(o, key);
        return super.put(key, o);
    }
}

