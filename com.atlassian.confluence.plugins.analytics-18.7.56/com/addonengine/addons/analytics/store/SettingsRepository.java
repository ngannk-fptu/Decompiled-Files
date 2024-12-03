/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store;

import com.addonengine.addons.analytics.store.model.DataRetentionSettingsData;
import com.addonengine.addons.analytics.store.model.EventLimitSettingsData;
import com.addonengine.addons.analytics.store.model.PrivacySettingsData;
import com.addonengine.addons.analytics.store.model.RateLimitSettingsData;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&J\u0010\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u0005H&J\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0007H&J\u0010\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\tH&J\u0010\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000bH&\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/store/SettingsRepository;", "", "deletePrivacySettings", "", "getDataRetentionSettings", "Lcom/addonengine/addons/analytics/store/model/DataRetentionSettingsData;", "getEventLimitSettings", "Lcom/addonengine/addons/analytics/store/model/EventLimitSettingsData;", "getPrivacySettings", "Lcom/addonengine/addons/analytics/store/model/PrivacySettingsData;", "getRateLimitSettings", "Lcom/addonengine/addons/analytics/store/model/RateLimitSettingsData;", "setDataRetentionSettings", "dataRetentionSettingsData", "setEventLimitSettings", "eventLimitSettingsData", "setPrivacySettings", "privacySettingsData", "setRateLimitSettings", "rateLimitSettingsData", "analytics"})
public interface SettingsRepository {
    @NotNull
    public PrivacySettingsData getPrivacySettings();

    @NotNull
    public DataRetentionSettingsData getDataRetentionSettings();

    @NotNull
    public EventLimitSettingsData getEventLimitSettings();

    @NotNull
    public RateLimitSettingsData getRateLimitSettings();

    @NotNull
    public PrivacySettingsData setPrivacySettings(@NotNull PrivacySettingsData var1);

    @NotNull
    public DataRetentionSettingsData setDataRetentionSettings(@NotNull DataRetentionSettingsData var1);

    @NotNull
    public EventLimitSettingsData setEventLimitSettings(@NotNull EventLimitSettingsData var1);

    @NotNull
    public RateLimitSettingsData setRateLimitSettings(@NotNull RateLimitSettingsData var1);

    public void deletePrivacySettings();
}

