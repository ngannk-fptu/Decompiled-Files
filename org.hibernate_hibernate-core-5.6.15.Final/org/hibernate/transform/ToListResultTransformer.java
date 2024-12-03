/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.Arrays;
import org.hibernate.transform.BasicTransformerAdapter;

public class ToListResultTransformer
extends BasicTransformerAdapter {
    public static final ToListResultTransformer INSTANCE = new ToListResultTransformer();

    private ToListResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return Arrays.asList(tuple);
    }

    private Object readResolve() {
        return INSTANCE;
    }
}

