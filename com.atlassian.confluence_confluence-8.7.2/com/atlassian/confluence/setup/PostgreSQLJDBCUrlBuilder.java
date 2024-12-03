/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.JDBCUrlBuilder;

@Internal
public class PostgreSQLJDBCUrlBuilder
implements JDBCUrlBuilder {
    private static final String FORMAT = "jdbc:postgresql://%s:%s/%s";

    @Override
    public String getDatabaseUrl(ConfluenceDatabaseDetails databaseDetails) {
        StringBuilder url = new StringBuilder(String.format(FORMAT, databaseDetails.getHostname(), databaseDetails.getPort(), databaseDetails.getDatabaseName()));
        return url.toString();
    }
}

