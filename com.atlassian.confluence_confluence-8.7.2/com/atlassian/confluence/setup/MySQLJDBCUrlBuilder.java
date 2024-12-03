/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.JDBCUrlBuilder;
import org.apache.commons.lang3.StringUtils;

@Internal
public class MySQLJDBCUrlBuilder
implements JDBCUrlBuilder {
    private static final String FORMAT = "jdbc:mysql://%s:%s/%s";
    private static final String FORMAT_WITHOUT_PORT = "jdbc:mysql://%s/%s";

    @Override
    public String getDatabaseUrl(ConfluenceDatabaseDetails databaseDetails) {
        if (StringUtils.isEmpty((CharSequence)databaseDetails.getPort())) {
            return String.format(FORMAT_WITHOUT_PORT, databaseDetails.getHostname(), databaseDetails.getDatabaseName());
        }
        return String.format(FORMAT, databaseDetails.getHostname(), databaseDetails.getPort(), databaseDetails.getDatabaseName());
    }
}

