/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.IdMap;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.identicator.StrongIdHashKey;
import java.util.HashMap;
import java.util.Map;

public final class IdHashMap
extends IdMap
implements Map {
    public IdHashMap(Identicator identicator) {
        super(new HashMap(), identicator);
    }

    @Override
    protected IdHashKey createIdKey(Object object) {
        return new StrongIdHashKey(object, this.id);
    }
}

