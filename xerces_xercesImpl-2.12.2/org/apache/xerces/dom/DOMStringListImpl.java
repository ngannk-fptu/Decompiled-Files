/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import java.util.Vector;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl
implements DOMStringList {
    private final ArrayList fStrings;

    public DOMStringListImpl() {
        this.fStrings = new ArrayList();
    }

    public DOMStringListImpl(ArrayList arrayList) {
        this.fStrings = arrayList;
    }

    public DOMStringListImpl(Vector vector) {
        this.fStrings = new ArrayList(vector);
    }

    @Override
    public String item(int n) {
        int n2 = this.getLength();
        if (n >= 0 && n < n2) {
            return (String)this.fStrings.get(n);
        }
        return null;
    }

    @Override
    public int getLength() {
        return this.fStrings.size();
    }

    @Override
    public boolean contains(String string) {
        return this.fStrings.contains(string);
    }

    public void add(String string) {
        this.fStrings.add(string);
    }
}

