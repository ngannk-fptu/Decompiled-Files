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
package com.addonengine.addons.analytics.rest.dto;

import com.addonengine.addons.analytics.rest.dto.AddonDetailsDto;
import com.addonengine.addons.analytics.rest.dto.SpaceDetailsDto;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/SpaceAnalyticsDetailsDto;", "", "addon", "Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;", "space", "Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;", "(Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;)V", "getAddon", "()Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;", "getSpace", "()Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SpaceAnalyticsDetailsDto {
    @NotNull
    private final AddonDetailsDto addon;
    @NotNull
    private final SpaceDetailsDto space;

    public SpaceAnalyticsDetailsDto(@NotNull AddonDetailsDto addon, @NotNull SpaceDetailsDto space) {
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        this.addon = addon;
        this.space = space;
    }

    @NotNull
    public final AddonDetailsDto getAddon() {
        return this.addon;
    }

    @NotNull
    public final SpaceDetailsDto getSpace() {
        return this.space;
    }

    @NotNull
    public final AddonDetailsDto component1() {
        return this.addon;
    }

    @NotNull
    public final SpaceDetailsDto component2() {
        return this.space;
    }

    @NotNull
    public final SpaceAnalyticsDetailsDto copy(@NotNull AddonDetailsDto addon, @NotNull SpaceDetailsDto space) {
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        return new SpaceAnalyticsDetailsDto(addon, space);
    }

    public static /* synthetic */ SpaceAnalyticsDetailsDto copy$default(SpaceAnalyticsDetailsDto spaceAnalyticsDetailsDto, AddonDetailsDto addonDetailsDto, SpaceDetailsDto spaceDetailsDto, int n, Object object) {
        if ((n & 1) != 0) {
            addonDetailsDto = spaceAnalyticsDetailsDto.addon;
        }
        if ((n & 2) != 0) {
            spaceDetailsDto = spaceAnalyticsDetailsDto.space;
        }
        return spaceAnalyticsDetailsDto.copy(addonDetailsDto, spaceDetailsDto);
    }

    @NotNull
    public String toString() {
        return "SpaceAnalyticsDetailsDto(addon=" + this.addon + ", space=" + this.space + ')';
    }

    public int hashCode() {
        int result = this.addon.hashCode();
        result = result * 31 + this.space.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceAnalyticsDetailsDto)) {
            return false;
        }
        SpaceAnalyticsDetailsDto spaceAnalyticsDetailsDto = (SpaceAnalyticsDetailsDto)other;
        if (!Intrinsics.areEqual((Object)this.addon, (Object)spaceAnalyticsDetailsDto.addon)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.space, (Object)spaceAnalyticsDetailsDto.space);
    }
}

