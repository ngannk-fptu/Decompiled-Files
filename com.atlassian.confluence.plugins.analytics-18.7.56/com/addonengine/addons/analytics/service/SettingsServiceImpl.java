/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KClass
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.HashService;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.EventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewDataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.NewEventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewPrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.NewRateLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.PrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.RateLimitSettings;
import com.addonengine.addons.analytics.store.SettingsRepository;
import com.addonengine.addons.analytics.store.model.DataRetentionSettingsData;
import com.addonengine.addons.analytics.store.model.EventLimitSettingsData;
import com.addonengine.addons.analytics.store.model.PrivacySettingsData;
import com.addonengine.addons.analytics.store.model.RateLimitSettingsData;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.util.profiling.UtilTimerStack;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ExportAsDevService(value={SettingsService.class})
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 62\u00020\u0001:\u000267B+\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u0010\u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\r\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u000e0\u000e0\fH\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\rH\u0002J\b\u0010\u0014\u001a\u00020\u0012H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001c\u001a\u00020\u001dJ\b\u0010\u001e\u001a\u00020\u001fH\u0016J\b\u0010 \u001a\u00020\u001fH\u0002J\b\u0010!\u001a\u00020\u0016H\u0016J\b\u0010\"\u001a\u00020#H\u0016J\b\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020&H\u0016J\b\u0010'\u001a\u00020&H\u0002J\b\u0010(\u001a\u00020)H\u0016J\u001f\u0010*\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010.H\u0016\u00a2\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020\u00162\u0006\u0010+\u001a\u000201H\u0016J\u0010\u00102\u001a\u00020#2\u0006\u0010+\u001a\u000203H\u0016J\u0010\u00104\u001a\u00020&2\u0006\u0010+\u001a\u000205H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u000b\u001a\u0018\u0012\u0006\u0012\u0004\u0018\u00010\r\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u000e0\u000e0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2={"Lcom/addonengine/addons/analytics/service/SettingsServiceImpl;", "Lcom/addonengine/addons/analytics/service/SettingsService;", "cacheManager", "Lcom/atlassian/cache/CacheManager;", "settingsRepository", "Lcom/addonengine/addons/analytics/store/SettingsRepository;", "hashService", "Lcom/addonengine/addons/analytics/service/HashService;", "timezoneManager", "Lcom/atlassian/sal/api/timezone/TimeZoneManager;", "(Lcom/atlassian/cache/CacheManager;Lcom/addonengine/addons/analytics/store/SettingsRepository;Lcom/addonengine/addons/analytics/service/HashService;Lcom/atlassian/sal/api/timezone/TimeZoneManager;)V", "settingsCache", "Lcom/atlassian/cache/Cache;", "Lcom/addonengine/addons/analytics/service/SettingsServiceImpl$Setting;", "", "kotlin.jvm.PlatformType", "buildSettingsCache", "clearCache", "", "setting", "clearPrivacySettings", "getDataLimitSettingsInternal", "Lcom/addonengine/addons/analytics/service/model/settings/EventLimitSettings;", "getDataRetentionActive", "", "effectiveFrom", "Ljava/time/Instant;", "getDataRetentionMinDate", "months", "", "getDataRetentionSettings", "Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "getDataRetentionSettingsInternal", "getEventLimitSettings", "getPrivacySettings", "Lcom/addonengine/addons/analytics/service/model/settings/PrivacySettings;", "getPrivacySettingsInternal", "getRateLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/RateLimitSettings;", "getRateLimitSettingsInternal", "serverTimezone", "Ljava/time/ZoneId;", "setDataRetentionSettings", "newSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewDataRetentionSettings;", "gracePeriod", "", "(Lcom/addonengine/addons/analytics/service/model/settings/NewDataRetentionSettings;Ljava/lang/Integer;)Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "setEventLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewEventLimitSettings;", "setPrivacySettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewPrivacySettings;", "setRateLimitSettings", "Lcom/addonengine/addons/analytics/service/model/settings/NewRateLimitSettings;", "Companion", "Setting", "analytics"})
@SourceDebugExtension(value={"SMAP\nSettingsServiceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SettingsServiceImpl.kt\ncom/addonengine/addons/analytics/service/SettingsServiceImpl\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n*L\n1#1,241:1\n11#2,11:242\n11#2,11:253\n11#2,11:264\n11#2,11:275\n*S KotlinDebug\n*F\n+ 1 SettingsServiceImpl.kt\ncom/addonengine/addons/analytics/service/SettingsServiceImpl\n*L\n47#1:242,11\n53#1:253,11\n59#1:264,11\n73#1:275,11\n*E\n"})
public final class SettingsServiceImpl
implements SettingsService {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final CacheManager cacheManager;
    @NotNull
    private final SettingsRepository settingsRepository;
    @NotNull
    private final HashService hashService;
    @NotNull
    private final TimeZoneManager timezoneManager;
    @NotNull
    private final Cache<Setting, Object> settingsCache;
    private static final int MIN_CONCURRENT_OPS_PER_SESSION = 4;

    @Inject
    public SettingsServiceImpl(@ComponentImport @NotNull CacheManager cacheManager, @NotNull SettingsRepository settingsRepository, @NotNull HashService hashService, @ComponentImport @NotNull TimeZoneManager timezoneManager) {
        Intrinsics.checkNotNullParameter((Object)cacheManager, (String)"cacheManager");
        Intrinsics.checkNotNullParameter((Object)settingsRepository, (String)"settingsRepository");
        Intrinsics.checkNotNullParameter((Object)hashService, (String)"hashService");
        Intrinsics.checkNotNullParameter((Object)timezoneManager, (String)"timezoneManager");
        this.cacheManager = cacheManager;
        this.settingsRepository = settingsRepository;
        this.hashService = hashService;
        this.timezoneManager = timezoneManager;
        this.settingsCache = this.buildSettingsCache();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public PrivacySettings getPrivacySettings() {
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "getPrivacySettings";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.settingsCache.get((Object)Setting.PRIVACY);
        Intrinsics.checkNotNull((Object)object);
        return (PrivacySettings)object;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public DataRetentionSettings getDataRetentionSettings() {
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "getDataRetentionSettings";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.settingsCache.get((Object)Setting.DATA_RETENTION);
        Intrinsics.checkNotNull((Object)object);
        return (DataRetentionSettings)object;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public RateLimitSettings getRateLimitSettings() {
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "getRateLimitSettings";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.settingsCache.get((Object)Setting.RATE_LIMIT);
        Intrinsics.checkNotNull((Object)object);
        return (RateLimitSettings)object;
    }

    public final boolean getDataRetentionActive(@NotNull Instant effectiveFrom) {
        Intrinsics.checkNotNullParameter((Object)effectiveFrom, (String)"effectiveFrom");
        return Instant.now().isAfter(effectiveFrom);
    }

    @NotNull
    public final Instant getDataRetentionMinDate(long months) {
        Instant instant = Instant.now().atOffset(ZoneOffset.UTC).minusMonths(months).truncatedTo(ChronoUnit.DAYS).toInstant();
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"toInstant(...)");
        return instant;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public EventLimitSettings getEventLimitSettings() {
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "getEventLimitSettings";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            void klass$iv;
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        Object object = this.settingsCache.get((Object)Setting.EVENT_LIMITS);
        Intrinsics.checkNotNull((Object)object);
        return (EventLimitSettings)object;
    }

    @Override
    @NotNull
    public PrivacySettings setPrivacySettings(@NotNull NewPrivacySettings newSettings) {
        PrivacySettingsData privacySettingsData;
        Intrinsics.checkNotNullParameter((Object)newSettings, (String)"newSettings");
        PrivacySettingsData existingSettingsData = this.settingsRepository.getPrivacySettings();
        if (newSettings.getEnabled() && existingSettingsData.getInstanceSalt() == null) {
            String instanceSalt = this.hashService.generateInstanceSalt();
            privacySettingsData = new PrivacySettingsData(newSettings.getEnabled(), instanceSalt);
        } else {
            privacySettingsData = new PrivacySettingsData(newSettings.getEnabled(), existingSettingsData.getInstanceSalt());
        }
        PrivacySettingsData newSettingsData = privacySettingsData;
        PrivacySettingsData savedPrivacyData = this.settingsRepository.setPrivacySettings(newSettingsData);
        this.clearCache(Setting.PRIVACY);
        return new PrivacySettings(savedPrivacyData.getEnabled(), savedPrivacyData.getInstanceSalt());
    }

    @Override
    @NotNull
    public DataRetentionSettings setDataRetentionSettings(@NotNull NewDataRetentionSettings newSettings, @Nullable Integer gracePeriod) {
        Intrinsics.checkNotNullParameter((Object)newSettings, (String)"newSettings");
        Integer n = gracePeriod;
        int gracePeriodDays = n != null ? n : (newSettings.getCustomised() ? 1 : 14);
        boolean bl = newSettings.getCustomised();
        int n2 = newSettings.getMonths();
        Instant instant = Instant.now().plus((long)gracePeriodDays, ChronoUnit.DAYS);
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"plus(...)");
        DataRetentionSettingsData savedSettings = this.settingsRepository.setDataRetentionSettings(new DataRetentionSettingsData(true, bl, n2, instant));
        this.clearCache(Setting.DATA_RETENTION);
        return new DataRetentionSettings(this.getDataRetentionActive(savedSettings.getEffectiveFrom()), savedSettings.getCustomised(), savedSettings.getMonths(), this.getDataRetentionMinDate(savedSettings.getMonths()));
    }

    @Override
    @NotNull
    public EventLimitSettings setEventLimitSettings(@NotNull NewEventLimitSettings newSettings) {
        Intrinsics.checkNotNullParameter((Object)newSettings, (String)"newSettings");
        EventLimitSettingsData savedSettings = this.settingsRepository.setEventLimitSettings(new EventLimitSettingsData(newSettings.getMaxRowCount()));
        this.clearCache(Setting.EVENT_LIMITS);
        return new EventLimitSettings(savedSettings.getMaxRowCount());
    }

    @Override
    @NotNull
    public RateLimitSettings setRateLimitSettings(@NotNull NewRateLimitSettings newSettings) {
        Intrinsics.checkNotNullParameter((Object)newSettings, (String)"newSettings");
        RateLimitSettingsData savedRateLimitData = this.settingsRepository.setRateLimitSettings(new RateLimitSettingsData(newSettings.getEnabled(), newSettings.getConcurrentSessions(), newSettings.getStaleOperationSeconds(), Math.max(MIN_CONCURRENT_OPS_PER_SESSION, newSettings.getConcurrentOperationsPerSession())));
        this.clearCache(Setting.RATE_LIMIT);
        return new RateLimitSettings(savedRateLimitData.getEnabled(), savedRateLimitData.getConcurrentSessions(), savedRateLimitData.getStaleOperationSeconds(), savedRateLimitData.getConcurrentOperationsPerSession());
    }

    @Override
    public void clearPrivacySettings() {
        this.settingsRepository.deletePrivacySettings();
        this.clearCache(Setting.PRIVACY);
    }

    @Override
    @NotNull
    public ZoneId serverTimezone() {
        ZoneId zoneId = ZoneId.of(this.timezoneManager.getDefaultTimeZone().getID());
        Intrinsics.checkNotNullExpressionValue((Object)zoneId, (String)"of(...)");
        return zoneId;
    }

    private final PrivacySettings getPrivacySettingsInternal() {
        PrivacySettingsData settings = this.settingsRepository.getPrivacySettings();
        return new PrivacySettings(settings.getEnabled(), settings.getInstanceSalt());
    }

    private final DataRetentionSettings getDataRetentionSettingsInternal() {
        DataRetentionSettingsData settings = this.settingsRepository.getDataRetentionSettings();
        int months = settings.getExists() ? settings.getMonths() : 12;
        return new DataRetentionSettings(settings.getExists() ? this.getDataRetentionActive(settings.getEffectiveFrom()) : true, settings.getExists() && settings.getCustomised(), months, this.getDataRetentionMinDate(months));
    }

    private final EventLimitSettings getDataLimitSettingsInternal() {
        EventLimitSettingsData settings = this.settingsRepository.getEventLimitSettings();
        return new EventLimitSettings(settings.getMaxRowCount());
    }

    private final RateLimitSettings getRateLimitSettingsInternal() {
        RateLimitSettingsData settings = this.settingsRepository.getRateLimitSettings();
        return new RateLimitSettings(settings.getEnabled(), settings.getConcurrentSessions(), settings.getStaleOperationSeconds(), Math.max(MIN_CONCURRENT_OPS_PER_SESSION, settings.getConcurrentOperationsPerSession()));
    }

    private final void clearCache(Setting setting) {
        this.settingsCache.remove((Object)setting);
    }

    private final Cache<Setting, Object> buildSettingsCache() {
        Cache cache = this.cacheManager.getCache("Analytics for Confluence - Settings", arg_0 -> SettingsServiceImpl.buildSettingsCache$lambda$4(this, arg_0), new CacheSettingsBuilder().remote().replicateViaInvalidation().replicateAsynchronously().maxEntries(100).expireAfterWrite(24L, TimeUnit.HOURS).build());
        Intrinsics.checkNotNullExpressionValue((Object)cache, (String)"getCache(...)");
        return cache;
    }

    private static final Object buildSettingsCache$lambda$4(SettingsServiceImpl this$0, Setting setting) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)((Object)setting), (String)"setting");
        switch (WhenMappings.$EnumSwitchMapping$0[setting.ordinal()]) {
            case 1: {
                object = this$0.getPrivacySettingsInternal();
                break;
            }
            case 2: {
                object = this$0.getDataRetentionSettingsInternal();
                break;
            }
            case 3: {
                object = this$0.getDataLimitSettingsInternal();
                break;
            }
            case 4: {
                object = this$0.getRateLimitSettingsInternal();
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return object;
    }

    public static final int getMIN_CONCURRENT_OPS_PER_SESSION() {
        return Companion.getMIN_CONCURRENT_OPS_PER_SESSION();
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001c\u0010\u0003\u001a\u00020\u00048\u0006X\u0087D\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0005\u0010\u0002\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/SettingsServiceImpl$Companion;", "", "()V", "MIN_CONCURRENT_OPS_PER_SESSION", "", "getMIN_CONCURRENT_OPS_PER_SESSION$annotations", "getMIN_CONCURRENT_OPS_PER_SESSION", "()I", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public final int getMIN_CONCURRENT_OPS_PER_SESSION() {
            return MIN_CONCURRENT_OPS_PER_SESSION;
        }

        @JvmStatic
        public static /* synthetic */ void getMIN_CONCURRENT_OPS_PER_SESSION$annotations() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/service/SettingsServiceImpl$Setting;", "", "(Ljava/lang/String;I)V", "PRIVACY", "DATA_RETENTION", "EVENT_LIMITS", "RATE_LIMIT", "analytics"})
    private static final class Setting
    extends Enum<Setting> {
        public static final /* enum */ Setting PRIVACY = new Setting();
        public static final /* enum */ Setting DATA_RETENTION = new Setting();
        public static final /* enum */ Setting EVENT_LIMITS = new Setting();
        public static final /* enum */ Setting RATE_LIMIT = new Setting();
        private static final /* synthetic */ Setting[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        public static Setting[] values() {
            return (Setting[])$VALUES.clone();
        }

        public static Setting valueOf(String value) {
            return Enum.valueOf(Setting.class, value);
        }

        @NotNull
        public static EnumEntries<Setting> getEntries() {
            return $ENTRIES;
        }

        static {
            $VALUES = settingArray = new Setting[]{Setting.PRIVACY, Setting.DATA_RETENTION, Setting.EVENT_LIMITS, Setting.RATE_LIMIT};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
        }
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[Setting.values().length];
            try {
                nArray[Setting.PRIVACY.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Setting.DATA_RETENTION.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Setting.EVENT_LIMITS.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Setting.RATE_LIMIT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

