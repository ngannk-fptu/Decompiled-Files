/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.upm.api.log;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.log.EntryType;
import java.util.Date;

public interface AuditLogEntry
extends Comparable<AuditLogEntry> {
    public String getTitle(I18nResolver var1);

    public String getMessage(I18nResolver var1);

    public Date getDate();

    @Deprecated
    public String getUsername();

    public String getUserKey();

    public String getI18nKey();

    public EntryType getEntryType();
}

