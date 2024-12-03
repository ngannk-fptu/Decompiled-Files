/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.IntChecklist;
import com.mchange.util.IntEnumeration;
import com.mchange.util.impl.IntObjectHash;

public class HashIntChecklist
implements IntChecklist {
    private static final Object DUMMY = new Object();
    IntObjectHash ioh = new IntObjectHash();

    @Override
    public void check(int n) {
        this.ioh.put(n, DUMMY);
    }

    @Override
    public void uncheck(int n) {
        this.ioh.remove(n);
    }

    @Override
    public boolean isChecked(int n) {
        return this.ioh.containsInt(n);
    }

    @Override
    public void clear() {
        this.ioh.clear();
    }

    @Override
    public int countChecked() {
        return this.ioh.getSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] getChecked() {
        IntObjectHash intObjectHash = this.ioh;
        synchronized (intObjectHash) {
            int[] nArray = new int[this.ioh.getSize()];
            IntEnumeration intEnumeration = this.ioh.ints();
            int n = 0;
            while (intEnumeration.hasMoreInts()) {
                nArray[n] = intEnumeration.nextInt();
                ++n;
            }
            return nArray;
        }
    }

    @Override
    public IntEnumeration checked() {
        return this.ioh.ints();
    }
}

