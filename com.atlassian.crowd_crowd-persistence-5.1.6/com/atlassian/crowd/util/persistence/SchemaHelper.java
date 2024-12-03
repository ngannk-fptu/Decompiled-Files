/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 */
package com.atlassian.crowd.util.persistence;

import com.atlassian.crowd.exception.OperationFailedException;
import java.sql.Connection;

public interface SchemaHelper {
    public void updateSchemaIfNeeded() throws OperationFailedException;

    public void createSchema() throws OperationFailedException;

    public boolean databaseContainsExistingData(Connection var1);
}

