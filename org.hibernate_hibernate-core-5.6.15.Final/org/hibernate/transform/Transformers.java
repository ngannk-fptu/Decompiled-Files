/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.ToListResultTransformer;

public final class Transformers {
    public static final AliasToEntityMapResultTransformer ALIAS_TO_ENTITY_MAP = AliasToEntityMapResultTransformer.INSTANCE;
    public static final ToListResultTransformer TO_LIST = ToListResultTransformer.INSTANCE;

    private Transformers() {
    }

    public static ResultTransformer aliasToBean(Class target) {
        return new AliasToBeanResultTransformer(target);
    }
}

