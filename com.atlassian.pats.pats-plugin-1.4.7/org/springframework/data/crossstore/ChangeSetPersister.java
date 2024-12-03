/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 */
package org.springframework.data.crossstore;

import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSet;
import org.springframework.data.crossstore.ChangeSetBacked;

public interface ChangeSetPersister<K> {
    public static final String ID_KEY = "_id";
    public static final String CLASS_KEY = "_class";

    public void getPersistentState(Class<? extends ChangeSetBacked> var1, K var2, ChangeSet var3) throws DataAccessException, NotFoundException;

    public K getPersistentId(ChangeSetBacked var1, ChangeSet var2) throws DataAccessException;

    public K persistState(ChangeSetBacked var1, ChangeSet var2) throws DataAccessException;

    public static class NotFoundException
    extends Exception {
        private static final long serialVersionUID = -8604207973816331140L;
    }
}

