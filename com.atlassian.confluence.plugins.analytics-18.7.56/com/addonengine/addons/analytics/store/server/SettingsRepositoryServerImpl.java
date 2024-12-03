/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.service.model.settings.RateLimitSettings;
import com.addonengine.addons.analytics.store.SettingsRepository;
import com.addonengine.addons.analytics.store.model.DataRetentionSettingsData;
import com.addonengine.addons.analytics.store.model.EventLimitSettingsData;
import com.addonengine.addons.analytics.store.model.PrivacySettingsData;
import com.addonengine.addons.analytics.store.model.RateLimitSettingsData;
import com.addonengine.addons.analytics.store.server.settings.Settings;
import com.addonengine.addons.analytics.store.server.settings.model.DataRetentionSetting;
import com.addonengine.addons.analytics.store.server.settings.model.EventLimitSetting;
import com.addonengine.addons.analytics.store.server.settings.model.PrivacySetting;
import com.addonengine.addons.analytics.store.server.settings.model.RateLimitSetting;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u000fH\u0016J\u0010\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u0011H\u0016J\u0010\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u0013H\u0016J\u0010\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u001d\u001a\u00020\u0015H\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2={"Lcom/addonengine/addons/analytics/store/server/SettingsRepositoryServerImpl;", "Lcom/addonengine/addons/analytics/store/SettingsRepository;", "settings", "Lcom/addonengine/addons/analytics/store/server/settings/Settings;", "(Lcom/addonengine/addons/analytics/store/server/settings/Settings;)V", "dataRetentionSettingsKey", "", "eventLimitSettingsKey", "maxEvents", "", "privacySettingsKey", "rateLimitSettingsKey", "deletePrivacySettings", "", "getDataRetentionSettings", "Lcom/addonengine/addons/analytics/store/model/DataRetentionSettingsData;", "getEventLimitSettings", "Lcom/addonengine/addons/analytics/store/model/EventLimitSettingsData;", "getPrivacySettings", "Lcom/addonengine/addons/analytics/store/model/PrivacySettingsData;", "getRateLimitSettings", "Lcom/addonengine/addons/analytics/store/model/RateLimitSettingsData;", "setDataRetentionSettings", "dataRetentionSettingsData", "setEventLimitSettings", "eventLimitSettingsData", "setPrivacySettings", "privacySettingsData", "setRateLimitSettings", "rateLimitSettingsData", "analytics"})
@SourceDebugExtension(value={"SMAP\nSettingsRepositoryServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SettingsRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/SettingsRepositoryServerImpl\n+ 2 Settings.kt\ncom/addonengine/addons/analytics/store/server/settings/Settings\n*L\n1#1,110:1\n25#2:111\n25#2:112\n25#2:113\n25#2:114\n*S KotlinDebug\n*F\n+ 1 SettingsRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/SettingsRepositoryServerImpl\n*L\n29#1:111\n39#1:112\n50#1:113\n58#1:114\n*E\n"})
public final class SettingsRepositoryServerImpl
implements SettingsRepository {
    @NotNull
    private final Settings settings;
    @NotNull
    private final String privacySettingsKey;
    @NotNull
    private final String dataRetentionSettingsKey;
    @NotNull
    private final String eventLimitSettingsKey;
    @NotNull
    private final String rateLimitSettingsKey;
    private final long maxEvents;

    @Inject
    public SettingsRepositoryServerImpl(@NotNull Settings settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        this.settings = settings;
        this.privacySettingsKey = "PRIVACY_SETTINGS";
        this.dataRetentionSettingsKey = "DATA_RETENTION_SETTINGS";
        this.eventLimitSettingsKey = "EVENT_LIMIT_SETTINGS";
        this.rateLimitSettingsKey = "RATE_LIMIT_SETTINGS";
        String string = System.getProperty("addonengine.analytics.eventLimiter.defaultMax");
        if (string == null) {
            string = "20000000";
        }
        this.maxEvents = Long.parseLong(string);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public PrivacySettingsData getPrivacySettings() {
        void this_$iv;
        PrivacySetting privacySetting;
        Settings settings = this.settings;
        String key$iv = this.privacySettingsKey;
        boolean $i$f$get = false;
        PrivacySetting privacySetting2 = privacySetting = (PrivacySetting)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(PrivacySetting.class));
        PrivacySetting privacySetting3 = privacySetting;
        return new PrivacySettingsData(privacySetting2 != null ? privacySetting2.getEnabled() : false, privacySetting3 != null ? privacySetting3.getInstanceSalt() : null);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public DataRetentionSettingsData getDataRetentionSettings() {
        void this_$iv;
        Settings settings = this.settings;
        String key$iv = this.dataRetentionSettingsKey;
        boolean $i$f$get = false;
        DataRetentionSetting dataRetentionSetting = (DataRetentionSetting)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(DataRetentionSetting.class));
        boolean bl = dataRetentionSetting != null;
        DataRetentionSetting dataRetentionSetting2 = dataRetentionSetting;
        boolean bl2 = dataRetentionSetting2 != null ? dataRetentionSetting2.getCustomised() : false;
        DataRetentionSetting dataRetentionSetting3 = dataRetentionSetting;
        int n = dataRetentionSetting3 != null ? dataRetentionSetting3.getMonths() : 0;
        DataRetentionSetting dataRetentionSetting4 = dataRetentionSetting;
        Instant instant = Instant.ofEpochMilli(dataRetentionSetting4 != null ? dataRetentionSetting4.getEffectiveFrom() : 0L);
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochMilli(...)");
        return new DataRetentionSettingsData(bl, bl2, n, instant);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public EventLimitSettingsData getEventLimitSettings() {
        void this_$iv;
        EventLimitSetting eventLimitSetting;
        Settings settings = this.settings;
        String key$iv = this.eventLimitSettingsKey;
        boolean $i$f$get = false;
        EventLimitSetting eventLimitSetting2 = eventLimitSetting = (EventLimitSetting)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(EventLimitSetting.class));
        return new EventLimitSettingsData(eventLimitSetting2 != null ? eventLimitSetting2.getMaxRowCount() : this.maxEvents);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public RateLimitSettingsData getRateLimitSettings() {
        void this_$iv;
        Settings settings = this.settings;
        String key$iv = this.rateLimitSettingsKey;
        boolean $i$f$get = false;
        RateLimitSettings rateLimitSettings = (RateLimitSettings)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(RateLimitSettings.class));
        if (rateLimitSettings == null) {
            rateLimitSettings = new RateLimitSettings(false, 0, 0L, 0, 15, null);
        }
        RateLimitSettings rateLimitSettings2 = rateLimitSettings;
        return new RateLimitSettingsData(rateLimitSettings2.getEnabled(), rateLimitSettings2.getConcurrentSessions(), rateLimitSettings2.getStaleOperationSeconds(), rateLimitSettings2.getConcurrentOperationsPerSession());
    }

    @Override
    @NotNull
    public PrivacySettingsData setPrivacySettings(@NotNull PrivacySettingsData privacySettingsData) {
        Intrinsics.checkNotNullParameter((Object)privacySettingsData, (String)"privacySettingsData");
        String string = privacySettingsData.getInstanceSalt();
        if (string == null) {
            throw new IllegalArgumentException("Instance salt is required when enabling privacy mode");
        }
        this.settings.set(this.privacySettingsKey, new PrivacySetting(privacySettingsData.getEnabled(), string));
        return privacySettingsData;
    }

    @Override
    @NotNull
    public DataRetentionSettingsData setDataRetentionSettings(@NotNull DataRetentionSettingsData dataRetentionSettingsData) {
        Intrinsics.checkNotNullParameter((Object)dataRetentionSettingsData, (String)"dataRetentionSettingsData");
        this.settings.set(this.dataRetentionSettingsKey, new DataRetentionSetting(dataRetentionSettingsData.getCustomised(), dataRetentionSettingsData.getMonths(), dataRetentionSettingsData.getEffectiveFrom().toEpochMilli()));
        return dataRetentionSettingsData;
    }

    @Override
    @NotNull
    public EventLimitSettingsData setEventLimitSettings(@NotNull EventLimitSettingsData eventLimitSettingsData) {
        Intrinsics.checkNotNullParameter((Object)eventLimitSettingsData, (String)"eventLimitSettingsData");
        this.settings.set(this.eventLimitSettingsKey, new EventLimitSetting(eventLimitSettingsData.getMaxRowCount()));
        return this.getEventLimitSettings();
    }

    @Override
    @NotNull
    public RateLimitSettingsData setRateLimitSettings(@NotNull RateLimitSettingsData rateLimitSettingsData) {
        Intrinsics.checkNotNullParameter((Object)rateLimitSettingsData, (String)"rateLimitSettingsData");
        this.settings.set(this.rateLimitSettingsKey, new RateLimitSetting(rateLimitSettingsData.getEnabled(), rateLimitSettingsData.getConcurrentSessions(), rateLimitSettingsData.getStaleOperationSeconds(), rateLimitSettingsData.getConcurrentOperationsPerSession()));
        return rateLimitSettingsData;
    }

    @Override
    public void deletePrivacySettings() {
        this.settings.delete(this.privacySettingsKey);
    }
}

