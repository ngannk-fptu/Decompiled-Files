/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.DatabaseDetails
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.setup.DatabaseCollationVerifier;
import com.atlassian.confluence.setup.DatabaseVerifyException;
import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseVerifier
extends DatabaseCollationVerifier {
    public void verifyDatabase(String var1, Connection var2) throws SQLException, DatabaseVerifyException;

    public void verifyDatasource(String var1, String var2) throws SQLException, DatabaseVerifyException;

    public void verifyDatabaseDetails(String var1, DatabaseDetails var2) throws SQLException, DatabaseVerifyException;
}

