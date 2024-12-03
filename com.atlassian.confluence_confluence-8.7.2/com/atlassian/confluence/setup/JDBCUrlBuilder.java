/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;

@Internal
public interface JDBCUrlBuilder {
    public String getDatabaseUrl(ConfluenceDatabaseDetails var1);
}

