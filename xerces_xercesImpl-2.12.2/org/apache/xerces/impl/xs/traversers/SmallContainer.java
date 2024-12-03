/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.traversers.Container;
import org.apache.xerces.impl.xs.traversers.OneAttr;

class SmallContainer
extends Container {
    String[] keys;

    SmallContainer(int n) {
        this.keys = new String[n];
        this.values = new OneAttr[n];
    }

    @Override
    void put(String string, OneAttr oneAttr) {
        this.keys[this.pos] = string;
        this.values[this.pos++] = oneAttr;
    }

    @Override
    OneAttr get(String string) {
        for (int i = 0; i < this.pos; ++i) {
            if (!this.keys[i].equals(string)) continue;
            return this.values[i];
        }
        return null;
    }
}

