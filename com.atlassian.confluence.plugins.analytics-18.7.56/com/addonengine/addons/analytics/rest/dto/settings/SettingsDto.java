/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto.settings;

import com.addonengine.addons.analytics.rest.dto.settings.DataRetentionSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.EventLimitSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.PrivacySettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.RateLimitSettingsDto;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\tH\u00c6\u0003J1\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001f"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/settings/SettingsDto;", "", "privacy", "Lcom/addonengine/addons/analytics/rest/dto/settings/PrivacySettingsDto;", "dataRetention", "Lcom/addonengine/addons/analytics/rest/dto/settings/DataRetentionSettingsDto;", "eventLimit", "Lcom/addonengine/addons/analytics/rest/dto/settings/EventLimitSettingsDto;", "rateLimit", "Lcom/addonengine/addons/analytics/rest/dto/settings/RateLimitSettingsDto;", "(Lcom/addonengine/addons/analytics/rest/dto/settings/PrivacySettingsDto;Lcom/addonengine/addons/analytics/rest/dto/settings/DataRetentionSettingsDto;Lcom/addonengine/addons/analytics/rest/dto/settings/EventLimitSettingsDto;Lcom/addonengine/addons/analytics/rest/dto/settings/RateLimitSettingsDto;)V", "getDataRetention", "()Lcom/addonengine/addons/analytics/rest/dto/settings/DataRetentionSettingsDto;", "getEventLimit", "()Lcom/addonengine/addons/analytics/rest/dto/settings/EventLimitSettingsDto;", "getPrivacy", "()Lcom/addonengine/addons/analytics/rest/dto/settings/PrivacySettingsDto;", "getRateLimit", "()Lcom/addonengine/addons/analytics/rest/dto/settings/RateLimitSettingsDto;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SettingsDto {
    @NotNull
    private final PrivacySettingsDto privacy;
    @NotNull
    private final DataRetentionSettingsDto dataRetention;
    @NotNull
    private final EventLimitSettingsDto eventLimit;
    @NotNull
    private final RateLimitSettingsDto rateLimit;

    public SettingsDto(@NotNull PrivacySettingsDto privacy, @NotNull DataRetentionSettingsDto dataRetention, @NotNull EventLimitSettingsDto eventLimit, @NotNull RateLimitSettingsDto rateLimit) {
        Intrinsics.checkNotNullParameter((Object)privacy, (String)"privacy");
        Intrinsics.checkNotNullParameter((Object)dataRetention, (String)"dataRetention");
        Intrinsics.checkNotNullParameter((Object)eventLimit, (String)"eventLimit");
        Intrinsics.checkNotNullParameter((Object)rateLimit, (String)"rateLimit");
        this.privacy = privacy;
        this.dataRetention = dataRetention;
        this.eventLimit = eventLimit;
        this.rateLimit = rateLimit;
    }

    @NotNull
    public final PrivacySettingsDto getPrivacy() {
        return this.privacy;
    }

    @NotNull
    public final DataRetentionSettingsDto getDataRetention() {
        return this.dataRetention;
    }

    @NotNull
    public final EventLimitSettingsDto getEventLimit() {
        return this.eventLimit;
    }

    @NotNull
    public final RateLimitSettingsDto getRateLimit() {
        return this.rateLimit;
    }

    @NotNull
    public final PrivacySettingsDto component1() {
        return this.privacy;
    }

    @NotNull
    public final DataRetentionSettingsDto component2() {
        return this.dataRetention;
    }

    @NotNull
    public final EventLimitSettingsDto component3() {
        return this.eventLimit;
    }

    @NotNull
    public final RateLimitSettingsDto component4() {
        return this.rateLimit;
    }

    @NotNull
    public final SettingsDto copy(@NotNull PrivacySettingsDto privacy, @NotNull DataRetentionSettingsDto dataRetention, @NotNull EventLimitSettingsDto eventLimit, @NotNull RateLimitSettingsDto rateLimit) {
        Intrinsics.checkNotNullParameter((Object)privacy, (String)"privacy");
        Intrinsics.checkNotNullParameter((Object)dataRetention, (String)"dataRetention");
        Intrinsics.checkNotNullParameter((Object)eventLimit, (String)"eventLimit");
        Intrinsics.checkNotNullParameter((Object)rateLimit, (String)"rateLimit");
        return new SettingsDto(privacy, dataRetention, eventLimit, rateLimit);
    }

    public static /* synthetic */ SettingsDto copy$default(SettingsDto settingsDto, PrivacySettingsDto privacySettingsDto, DataRetentionSettingsDto dataRetentionSettingsDto, EventLimitSettingsDto eventLimitSettingsDto, RateLimitSettingsDto rateLimitSettingsDto, int n, Object object) {
        if ((n & 1) != 0) {
            privacySettingsDto = settingsDto.privacy;
        }
        if ((n & 2) != 0) {
            dataRetentionSettingsDto = settingsDto.dataRetention;
        }
        if ((n & 4) != 0) {
            eventLimitSettingsDto = settingsDto.eventLimit;
        }
        if ((n & 8) != 0) {
            rateLimitSettingsDto = settingsDto.rateLimit;
        }
        return settingsDto.copy(privacySettingsDto, dataRetentionSettingsDto, eventLimitSettingsDto, rateLimitSettingsDto);
    }

    @NotNull
    public String toString() {
        return "SettingsDto(privacy=" + this.privacy + ", dataRetention=" + this.dataRetention + ", eventLimit=" + this.eventLimit + ", rateLimit=" + this.rateLimit + ')';
    }

    public int hashCode() {
        int result = this.privacy.hashCode();
        result = result * 31 + this.dataRetention.hashCode();
        result = result * 31 + this.eventLimit.hashCode();
        result = result * 31 + this.rateLimit.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SettingsDto)) {
            return false;
        }
        SettingsDto settingsDto = (SettingsDto)other;
        if (!Intrinsics.areEqual((Object)this.privacy, (Object)settingsDto.privacy)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.dataRetention, (Object)settingsDto.dataRetention)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.eventLimit, (Object)settingsDto.eventLimit)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.rateLimit, (Object)settingsDto.rateLimit);
    }
}

