/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.traversers.LargeContainer;
import org.apache.xerces.impl.xs.traversers.OneAttr;
import org.apache.xerces.impl.xs.traversers.SmallContainer;

abstract class Container {
    static final int THRESHOLD = 5;
    OneAttr[] values;
    int pos = 0;

    Container() {
    }

    static Container getContainer(int n) {
        if (n > 5) {
            return new LargeContainer(n);
        }
        return new SmallContainer(n);
    }

    abstract void put(String var1, OneAttr var2);

    abstract OneAttr get(String var1);
}

