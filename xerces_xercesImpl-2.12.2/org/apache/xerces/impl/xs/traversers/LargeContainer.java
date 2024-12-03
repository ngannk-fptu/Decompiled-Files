/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.util.Hashtable;
import org.apache.xerces.impl.xs.traversers.Container;
import org.apache.xerces.impl.xs.traversers.OneAttr;

class LargeContainer
extends Container {
    Hashtable items;

    LargeContainer(int n) {
        this.items = new Hashtable(n * 2 + 1);
        this.values = new OneAttr[n];
    }

    @Override
    void put(String string, OneAttr oneAttr) {
        this.items.put(string, oneAttr);
        this.values[this.pos++] = oneAttr;
    }

    @Override
    OneAttr get(String string) {
        OneAttr oneAttr = (OneAttr)this.items.get(string);
        return oneAttr;
    }
}

