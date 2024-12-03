/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.HashMap;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

public class AliasToEntityMapResultTransformer
extends AliasedTupleSubsetResultTransformer {
    public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();

    private AliasToEntityMapResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        HashMap<String, Object> result = new HashMap<String, Object>(tuple.length);
        for (int i = 0; i < tuple.length; ++i) {
            String alias = aliases[i];
            if (alias == null) continue;
            result.put(alias, tuple[i]);
        }
        return result;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

