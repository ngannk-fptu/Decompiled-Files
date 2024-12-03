/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.Connection
 *  javax.resource.cci.ConnectionFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.cci.core;

import java.sql.SQLException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@Deprecated
@FunctionalInterface
public interface ConnectionCallback<T> {
    @Nullable
    public T doInConnection(Connection var1, ConnectionFactory var2) throws ResourceException, SQLException, DataAccessException;
}

