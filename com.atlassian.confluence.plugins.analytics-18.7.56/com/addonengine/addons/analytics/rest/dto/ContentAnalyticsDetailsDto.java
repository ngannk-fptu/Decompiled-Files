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
import com.addonengine.addons.analytics.rest.dto.ContentDetailsDto;
import com.addonengine.addons.analytics.rest.dto.SpaceDetailsDto;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u001a"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ContentAnalyticsDetailsDto;", "", "addon", "Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;", "content", "Lcom/addonengine/addons/analytics/rest/dto/ContentDetailsDto;", "space", "Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;", "(Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;Lcom/addonengine/addons/analytics/rest/dto/ContentDetailsDto;Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;)V", "getAddon", "()Lcom/addonengine/addons/analytics/rest/dto/AddonDetailsDto;", "getContent", "()Lcom/addonengine/addons/analytics/rest/dto/ContentDetailsDto;", "getSpace", "()Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ContentAnalyticsDetailsDto {
    @NotNull
    private final AddonDetailsDto addon;
    @NotNull
    private final ContentDetailsDto content;
    @NotNull
    private final SpaceDetailsDto space;

    public ContentAnalyticsDetailsDto(@NotNull AddonDetailsDto addon, @NotNull ContentDetailsDto content, @NotNull SpaceDetailsDto space) {
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        Intrinsics.checkNotNullParameter((Object)content, (String)"content");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        this.addon = addon;
        this.content = content;
        this.space = space;
    }

    @NotNull
    public final AddonDetailsDto getAddon() {
        return this.addon;
    }

    @NotNull
    public final ContentDetailsDto getContent() {
        return this.content;
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
    public final ContentDetailsDto component2() {
        return this.content;
    }

    @NotNull
    public final SpaceDetailsDto component3() {
        return this.space;
    }

    @NotNull
    public final ContentAnalyticsDetailsDto copy(@NotNull AddonDetailsDto addon, @NotNull ContentDetailsDto content, @NotNull SpaceDetailsDto space) {
        Intrinsics.checkNotNullParameter((Object)addon, (String)"addon");
        Intrinsics.checkNotNullParameter((Object)content, (String)"content");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        return new ContentAnalyticsDetailsDto(addon, content, space);
    }

    public static /* synthetic */ ContentAnalyticsDetailsDto copy$default(ContentAnalyticsDetailsDto contentAnalyticsDetailsDto, AddonDetailsDto addonDetailsDto, ContentDetailsDto contentDetailsDto, SpaceDetailsDto spaceDetailsDto, int n, Object object) {
        if ((n & 1) != 0) {
            addonDetailsDto = contentAnalyticsDetailsDto.addon;
        }
        if ((n & 2) != 0) {
            contentDetailsDto = contentAnalyticsDetailsDto.content;
        }
        if ((n & 4) != 0) {
            spaceDetailsDto = contentAnalyticsDetailsDto.space;
        }
        return contentAnalyticsDetailsDto.copy(addonDetailsDto, contentDetailsDto, spaceDetailsDto);
    }

    @NotNull
    public String toString() {
        return "ContentAnalyticsDetailsDto(addon=" + this.addon + ", content=" + this.content + ", space=" + this.space + ')';
    }

    public int hashCode() {
        int result = this.addon.hashCode();
        result = result * 31 + this.content.hashCode();
        result = result * 31 + this.space.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentAnalyticsDetailsDto)) {
            return false;
        }
        ContentAnalyticsDetailsDto contentAnalyticsDetailsDto = (ContentAnalyticsDetailsDto)other;
        if (!Intrinsics.areEqual((Object)this.addon, (Object)contentAnalyticsDetailsDto.addon)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.content, (Object)contentAnalyticsDetailsDto.content)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.space, (Object)contentAnalyticsDetailsDto.space);
    }
}

