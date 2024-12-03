/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.server;

import java.util.Collection;
import java.util.Vector;

public class ParamList
extends Vector {
    public ParamList() {
    }

    public ParamList(Collection c) {
        super(c);
    }

    public ParamList(int initialCapacity) {
        super(initialCapacity);
    }

    public ParamList(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }
}

