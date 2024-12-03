/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.ReminderSettingCallback;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.fugue.Option;
import java.util.List;

public interface CustomEventTypeSupport<T> {
    public CustomEventTypeEntity updateCustomEventType(Option<ReminderSettingCallback> var1, T var2, String var3, String var4, String var5, int var6);

    public CustomEventTypeEntity getCustomEventType(T var1, String var2);

    public List<CustomEventTypeEntity> getCustomEventTypes(String ... var1);

    public void deleteCustomEventType(String var1, String var2);
}

