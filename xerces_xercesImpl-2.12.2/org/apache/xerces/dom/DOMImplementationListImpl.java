/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl
implements DOMImplementationList {
    private final ArrayList fImplementations;

    public DOMImplementationListImpl() {
        this.fImplementations = new ArrayList();
    }

    public DOMImplementationListImpl(ArrayList arrayList) {
        this.fImplementations = arrayList;
    }

    public DOMImplementationListImpl(Vector vector) {
        this.fImplementations = new ArrayList(vector);
    }

    @Override
    public DOMImplementation item(int n) {
        int n2 = this.getLength();
        if (n >= 0 && n < n2) {
            return (DOMImplementation)this.fImplementations.get(n);
        }
        return null;
    }

    @Override
    public int getLength() {
        return this.fImplementations.size();
    }
}

