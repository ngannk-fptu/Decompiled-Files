/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.EventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewDataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.NewEventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewPrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.NewRateLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.PrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.RateLimitSettings;
import java.time.ZoneId;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&J\b\u0010\f\u001a\u00020\rH&J!\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012H&\u00a2\u0006\u0002\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0015H&J\u0010\u0010\u0016\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u0017H&J\u0010\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0019H&\u00a8\u0006\u001a"}, d2={"Lcom/addonengine/addons/analytics/service/SettingsService;", "", "clearPrivacySettings", "", "getDataRetentionSettings", "Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "getEventLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/EventLimitSettings;", "getPrivacySettings", "Lcom/addonengine/addons/analytics/service/model/settings/PrivacySettings;", "getRateLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/RateLimitSettings;", "serverTimezone", "Ljava/time/ZoneId;", "setDataRetentionSettings", "newSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewDataRetentionSettings;", "gracePeriod", "", "(Lcom/addonengine/addons/analytics/service/model/settings/NewDataRetentionSettings;Ljava/lang/Integer;)Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "setEventLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewEventLimitSettings;", "setPrivacySettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewPrivacySettings;", "setRateLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewRateLimitSettings;", "analytics"})
public interface SettingsService {
    @NotNull
    public PrivacySettings getPrivacySettings();

    @NotNull
    public DataRetentionSettings getDataRetentionSettings();

    @NotNull
    public EventLimitSettings getEventLimitSettings();

    @NotNull
    public RateLimitSettings getRateLimitSettings();

    @NotNull
    public PrivacySettings setPrivacySettings(@NotNull NewPrivacySettings var1);

    @NotNull
    public DataRetentionSettings setDataRetentionSettings(@NotNull NewDataRetentionSettings var1, @Nullable Integer var2);

    @NotNull
    public RateLimitSettings setRateLimitSettings(@NotNull NewRateLimitSettings var1);

    public void clearPrivacySettings();

    @NotNull
    public ZoneId serverTimezone();

    @NotNull
    public EventLimitSettings setEventLimitSettings(@NotNull NewEventLimitSettings var1);

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        public static /* synthetic */ DataRetentionSettings setDataRetentionSettings$default(SettingsService settingsService, NewDataRetentionSettings newDataRetentionSettings, Integer n, int n2, Object object) {
            if (object != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: setDataRetentionSettings");
            }
            if ((n2 & 2) != 0) {
                n = null;
            }
            return settingsService.setDataRetentionSettings(newDataRetentionSettings, n);
        }
    }
}

