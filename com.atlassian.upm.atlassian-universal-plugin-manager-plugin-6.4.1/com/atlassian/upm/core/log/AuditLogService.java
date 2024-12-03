/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.rometools.rome.feed.atom.Feed
 */
package com.atlassian.upm.core.log;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import com.rometools.rome.feed.atom.Feed;
import java.util.Set;

public interface AuditLogService {
    public void logI18nMessage(String var1, String ... var2);

    public void logI18nMessageWithUserKey(String var1, UserKey var2, String ... var3);

    public void logI18nMessageWithCurrentApplication(String var1, String ... var2);

    public Iterable<AuditLogEntry> getLogEntries();

    public Iterable<AuditLogEntry> getLogEntries(Integer var1, Integer var2);

    public Iterable<AuditLogEntry> getLogEntries(Integer var1, Integer var2, Set<EntryType> var3);

    public Feed getFeed();

    public Feed getFeed(Integer var1, Integer var2);

    public void purgeLog();

    public int getMaxEntries();

    public void setMaxEntries(int var1);

    public int getPurgeAfter();

    public void setPurgeAfter(int var1);
}

