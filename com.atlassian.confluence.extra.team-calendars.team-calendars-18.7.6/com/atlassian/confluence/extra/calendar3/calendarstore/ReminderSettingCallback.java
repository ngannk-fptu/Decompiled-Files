/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;

public interface ReminderSettingCallback {
    public void createReminderSetting(ReminderSettingChange var1, PersistedSubCalendar var2);

    public void updateReminderSetting(ReminderSettingChange var1, PersistedSubCalendar var2);

    public static class ReminderSettingChange {
        int oldPeriodInMins;
        int newPeriodInMins;
        String storeKey;
        String customEventTypeId;

        public ReminderSettingChange(String storeKey, String customEventTypeId, int oldPeriodInMins, int newPeriodInMins) {
            this.oldPeriodInMins = oldPeriodInMins;
            this.newPeriodInMins = newPeriodInMins;
            this.storeKey = storeKey;
            this.customEventTypeId = customEventTypeId;
        }

        public String getStoreKey() {
            return this.storeKey;
        }

        public void setStoreKey(String storeKey) {
            this.storeKey = storeKey;
        }

        public String getCustomEventTypeId() {
            return this.customEventTypeId;
        }

        public void setCustomEventTypeId(String customEventTypeId) {
            this.customEventTypeId = customEventTypeId;
        }

        public int getOldPeriodInMins() {
            return this.oldPeriodInMins;
        }

        public void setOldPeriodInMins(int oldPeriodInMins) {
            this.oldPeriodInMins = oldPeriodInMins;
        }

        public int getNewPeriodInMins() {
            return this.newPeriodInMins;
        }

        public void setNewPeriodInMins(int newPeriodInMins) {
            this.newPeriodInMins = newPeriodInMins;
        }
    }
}

