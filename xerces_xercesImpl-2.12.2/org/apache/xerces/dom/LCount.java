/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.Hashtable;

class LCount {
    static Hashtable lCounts = new Hashtable();
    public int captures = 0;
    public int bubbles = 0;
    public int defaults;
    public int total = 0;

    LCount() {
    }

    static LCount lookup(String string) {
        LCount lCount = (LCount)lCounts.get(string);
        if (lCount == null) {
            lCount = new LCount();
            lCounts.put(string, lCount);
        }
        return lCount;
    }
}

