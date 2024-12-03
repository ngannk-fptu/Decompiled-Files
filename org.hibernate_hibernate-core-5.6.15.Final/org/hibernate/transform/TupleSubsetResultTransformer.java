/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import org.hibernate.transform.ResultTransformer;

public interface TupleSubsetResultTransformer
extends ResultTransformer {
    public boolean isTransformedValueATupleElement(String[] var1, int var2);

    public boolean[] includeInTransform(String[] var1, int var2);
}

