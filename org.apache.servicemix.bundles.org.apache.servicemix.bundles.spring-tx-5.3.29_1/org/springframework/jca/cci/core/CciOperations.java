/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.cci.InteractionSpec
 *  javax.resource.cci.Record
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.cci.core;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import org.springframework.dao.DataAccessException;
import org.springframework.jca.cci.core.ConnectionCallback;
import org.springframework.jca.cci.core.InteractionCallback;
import org.springframework.jca.cci.core.RecordCreator;
import org.springframework.jca.cci.core.RecordExtractor;
import org.springframework.lang.Nullable;

@Deprecated
public interface CciOperations {
    @Nullable
    public <T> T execute(ConnectionCallback<T> var1) throws DataAccessException;

    @Nullable
    public <T> T execute(InteractionCallback<T> var1) throws DataAccessException;

    @Nullable
    public Record execute(InteractionSpec var1, Record var2) throws DataAccessException;

    public void execute(InteractionSpec var1, Record var2, Record var3) throws DataAccessException;

    public Record execute(InteractionSpec var1, RecordCreator var2) throws DataAccessException;

    @Nullable
    public <T> T execute(InteractionSpec var1, Record var2, RecordExtractor<T> var3) throws DataAccessException;

    @Nullable
    public <T> T execute(InteractionSpec var1, RecordCreator var2, RecordExtractor<T> var3) throws DataAccessException;
}

