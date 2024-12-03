/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigValue;
import java.util.Collection;

enum ResolveStatus {
    UNRESOLVED,
    RESOLVED;


    static final ResolveStatus fromValues(Collection<? extends AbstractConfigValue> values) {
        for (AbstractConfigValue abstractConfigValue : values) {
            if (abstractConfigValue.resolveStatus() != UNRESOLVED) continue;
            return UNRESOLVED;
        }
        return RESOLVED;
    }

    static final ResolveStatus fromBoolean(boolean resolved) {
        return resolved ? RESOLVED : UNRESOLVED;
    }
}

