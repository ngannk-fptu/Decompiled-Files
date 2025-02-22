/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.Arrays;
import java.util.List;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.TupleSubsetResultTransformer;

public class PassThroughResultTransformer
extends BasicTransformerAdapter
implements TupleSubsetResultTransformer {
    public static final PassThroughResultTransformer INSTANCE = new PassThroughResultTransformer();

    private PassThroughResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple.length == 1 ? tuple[0] : tuple;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return tupleLength == 1;
    }

    @Override
    public boolean[] includeInTransform(String[] aliases, int tupleLength) {
        boolean[] includeInTransformedResult = new boolean[tupleLength];
        Arrays.fill(includeInTransformedResult, true);
        return includeInTransformedResult;
    }

    List untransformToTuples(List results, boolean isSingleResult) {
        if (isSingleResult) {
            for (int i = 0; i < results.size(); ++i) {
                Object[] tuple = this.untransformToTuple(results.get(i), isSingleResult);
                results.set(i, tuple);
            }
        }
        return results;
    }

    Object[] untransformToTuple(Object transformed, boolean isSingleResult) {
        Object[] objectArray;
        if (isSingleResult) {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = transformed;
        } else {
            objectArray = (Object[])transformed;
        }
        return objectArray;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

