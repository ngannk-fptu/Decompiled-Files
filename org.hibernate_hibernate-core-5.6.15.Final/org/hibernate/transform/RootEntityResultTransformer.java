/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.TupleSubsetResultTransformer;

public final class RootEntityResultTransformer
extends BasicTransformerAdapter
implements TupleSubsetResultTransformer {
    public static final RootEntityResultTransformer INSTANCE = new RootEntityResultTransformer();

    private RootEntityResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple[tuple.length - 1];
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return true;
    }

    @Override
    public boolean[] includeInTransform(String[] aliases, int tupleLength) {
        boolean[] includeInTransform;
        if (tupleLength == 1) {
            includeInTransform = ArrayHelper.TRUE;
        } else {
            includeInTransform = new boolean[tupleLength];
            includeInTransform[tupleLength - 1] = true;
        }
        return includeInTransform;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

