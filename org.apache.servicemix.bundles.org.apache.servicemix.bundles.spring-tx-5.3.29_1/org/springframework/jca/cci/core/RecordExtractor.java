/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Record
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.cci.core;

import java.sql.SQLException;
import javax.resource.ResourceException;
import javax.resource.cci.Record;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@Deprecated
@FunctionalInterface
public interface RecordExtractor<T> {
    @Nullable
    public T extractData(Record var1) throws ResourceException, SQLException, DataAccessException;
}

