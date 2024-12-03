/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.SpringLdapPoolStatistics;
import java.util.Map;

public interface SpringLdapPoolStatisticsProvider {
    public Map<Long, SpringLdapPoolStatistics> getPoolStatistics();
}

