/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.xs.models.XSCMLeaf;

public final class XSCMRepeatingLeaf
extends XSCMLeaf {
    private final int fMinOccurs;
    private final int fMaxOccurs;

    public XSCMRepeatingLeaf(int n, Object object, int n2, int n3, int n4, int n5) {
        super(n, object, n4, n5);
        this.fMinOccurs = n2;
        this.fMaxOccurs = n3;
    }

    final int getMinOccurs() {
        return this.fMinOccurs;
    }

    final int getMaxOccurs() {
        return this.fMaxOccurs;
    }
}

