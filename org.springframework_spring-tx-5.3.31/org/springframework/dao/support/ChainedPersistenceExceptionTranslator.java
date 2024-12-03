/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.dao.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ChainedPersistenceExceptionTranslator
implements PersistenceExceptionTranslator {
    private final List<PersistenceExceptionTranslator> delegates = new ArrayList<PersistenceExceptionTranslator>(4);

    public final void addDelegate(PersistenceExceptionTranslator pet) {
        Assert.notNull((Object)pet, (String)"PersistenceExceptionTranslator must not be null");
        this.delegates.add(pet);
    }

    public final PersistenceExceptionTranslator[] getDelegates() {
        return this.delegates.toArray(new PersistenceExceptionTranslator[0]);
    }

    @Override
    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        for (PersistenceExceptionTranslator pet : this.delegates) {
            DataAccessException translatedDex = pet.translateExceptionIfPossible(ex);
            if (translatedDex == null) continue;
            return translatedDex;
        }
        return null;
    }
}

