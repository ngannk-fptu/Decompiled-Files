/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.cci.ConnectionFactory
 *  javax.resource.cci.Interaction
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.cci.core;

import java.sql.SQLException;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@Deprecated
@FunctionalInterface
public interface InteractionCallback<T> {
    @Nullable
    public T doInInteraction(Interaction var1, ConnectionFactory var2) throws ResourceException, SQLException, DataAccessException;
}

