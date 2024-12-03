/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao.support;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface PersistenceExceptionTranslator {
    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException var1);
}

