/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.List;
import org.hibernate.transform.ResultTransformer;

public abstract class BasicTransformerAdapter
implements ResultTransformer {
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return tuple;
    }

    @Override
    public List transformList(List list) {
        return list;
    }
}

