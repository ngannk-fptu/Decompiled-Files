/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.List;
import org.hibernate.transform.DistinctResultTransformer;
import org.hibernate.transform.RootEntityResultTransformer;
import org.hibernate.transform.TupleSubsetResultTransformer;

public class DistinctRootEntityResultTransformer
implements TupleSubsetResultTransformer {
    public static final DistinctRootEntityResultTransformer INSTANCE = new DistinctRootEntityResultTransformer();

    private DistinctRootEntityResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return RootEntityResultTransformer.INSTANCE.transformTuple(tuple, aliases);
    }

    @Override
    public List transformList(List list) {
        return DistinctResultTransformer.INSTANCE.transformList(list);
    }

    @Override
    public boolean[] includeInTransform(String[] aliases, int tupleLength) {
        return RootEntityResultTransformer.INSTANCE.includeInTransform(aliases, tupleLength);
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return RootEntityResultTransformer.INSTANCE.isTransformedValueATupleElement(null, tupleLength);
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

