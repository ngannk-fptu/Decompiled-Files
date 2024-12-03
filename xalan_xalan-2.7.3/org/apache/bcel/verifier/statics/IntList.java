/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import java.util.ArrayList;
import java.util.List;

public class IntList {
    private final List<Integer> list = new ArrayList<Integer>();

    IntList() {
    }

    void add(int i) {
        this.list.add(i);
    }

    boolean contains(int i) {
        return this.list.contains(i);
    }
}

