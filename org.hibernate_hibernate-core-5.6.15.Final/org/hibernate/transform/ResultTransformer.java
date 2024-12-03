/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.io.Serializable;
import java.util.List;

public interface ResultTransformer
extends Serializable {
    public Object transformTuple(Object[] var1, String[] var2);

    public List transformList(List var1);
}

