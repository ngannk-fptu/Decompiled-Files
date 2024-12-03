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
public class OracleJDBCUrlBuilder
implements JDBCUrlBuilder {
    private static final String FORMAT = "jdbc:oracle:thin:@//%s:%s/%s";

    @Override
    public String getDatabaseUrl(ConfluenceDatabaseDetails databaseDetails) {
        return String.format(FORMAT, databaseDetails.getHostname(), databaseDetails.getPort(), databaseDetails.getServiceName());
    }
}

