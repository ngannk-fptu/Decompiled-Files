/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex.util;

import java.util.ArrayList;
import java.util.Collection;

public final class FinalArrayList<T>
extends ArrayList<T> {
    public FinalArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public FinalArrayList() {
    }

    public FinalArrayList(Collection collection) {
        super(collection);
    }
}

