/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.reminder;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;

public interface RemindingSettingHelper {
    public void enableRemindingFor(ConfluenceUser var1, PersistedSubCalendar var2);

    public void enableRemindingForWatcher(Collection<ConfluenceUser> var1, PersistedSubCalendar var2);
}

