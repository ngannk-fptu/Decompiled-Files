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
public class SQLServerJDBCUrlBuilder
implements JDBCUrlBuilder {
    @Override
    public String getDatabaseUrl(ConfluenceDatabaseDetails databaseDetails) {
        StringBuilder url = new StringBuilder("jdbc:sqlserver://");
        url.append(databaseDetails.getHostname());
        if (StringUtils.isEmpty((CharSequence)databaseDetails.getInstanceName()) && !StringUtils.isEmpty((CharSequence)databaseDetails.getPort())) {
            url.append(":").append(databaseDetails.getPort());
        }
        url.append(";databaseName=").append(databaseDetails.getDatabaseName());
        if (!StringUtils.isEmpty((CharSequence)databaseDetails.getInstanceName())) {
            url.append(";instanceName=").append(databaseDetails.getInstanceName());
        }
        url.append(";encrypt=false");
        return url.toString();
    }
}

