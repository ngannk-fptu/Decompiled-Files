/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.io.OutputStream;

public interface ICalendarExporter {
    public void export(ConfluenceUser var1, PersistedSubCalendar var2, OutputStream var3, boolean var4) throws Exception;
}

