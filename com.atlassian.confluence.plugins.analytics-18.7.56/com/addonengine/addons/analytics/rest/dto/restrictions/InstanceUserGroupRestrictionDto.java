/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto.restrictions;

import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/InstanceUserGroupRestrictionDto;", "", "groupName", "", "profilePictureUrl", "Ljava/net/URL;", "useAnalytics", "", "(Ljava/lang/String;Ljava/net/URL;Z)V", "getGroupName", "()Ljava/lang/String;", "getProfilePictureUrl", "()Ljava/net/URL;", "getUseAnalytics", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "analytics"})
public final class InstanceUserGroupRestrictionDto {
    @NotNull
    private final String groupName;
    @NotNull
    private final URL profilePictureUrl;
    private final boolean useAnalytics;

    public InstanceUserGroupRestrictionDto(@JsonProperty(value="groupName") @NotNull String groupName, @JsonProperty(value="profilePictureUrl") @NotNull URL profilePictureUrl, @JsonProperty(value="useAnalytics") boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)groupName, (String)"groupName");
        Intrinsics.checkNotNullParameter((Object)profilePictureUrl, (String)"profilePictureUrl");
        this.groupName = groupName;
        this.profilePictureUrl = profilePictureUrl;
        this.useAnalytics = useAnalytics;
    }

    @NotNull
    public final String getGroupName() {
        return this.groupName;
    }

    @NotNull
    public final URL getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public final boolean getUseAnalytics() {
        return this.useAnalytics;
    }

    @NotNull
    public final String component1() {
        return this.groupName;
    }

    @NotNull
    public final URL component2() {
        return this.profilePictureUrl;
    }

    public final boolean component3() {
        return this.useAnalytics;
    }

    @NotNull
    public final InstanceUserGroupRestrictionDto copy(@JsonProperty(value="groupName") @NotNull String groupName, @JsonProperty(value="profilePictureUrl") @NotNull URL profilePictureUrl, @JsonProperty(value="useAnalytics") boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)groupName, (String)"groupName");
        Intrinsics.checkNotNullParameter((Object)profilePictureUrl, (String)"profilePictureUrl");
        return new InstanceUserGroupRestrictionDto(groupName, profilePictureUrl, useAnalytics);
    }

    public static /* synthetic */ InstanceUserGroupRestrictionDto copy$default(InstanceUserGroupRestrictionDto instanceUserGroupRestrictionDto, String string, URL uRL, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            string = instanceUserGroupRestrictionDto.groupName;
        }
        if ((n & 2) != 0) {
            uRL = instanceUserGroupRestrictionDto.profilePictureUrl;
        }
        if ((n & 4) != 0) {
            bl = instanceUserGroupRestrictionDto.useAnalytics;
        }
        return instanceUserGroupRestrictionDto.copy(string, uRL, bl);
    }

    @NotNull
    public String toString() {
        return "InstanceUserGroupRestrictionDto(groupName=" + this.groupName + ", profilePictureUrl=" + this.profilePictureUrl + ", useAnalytics=" + this.useAnalytics + ')';
    }

    public int hashCode() {
        int result = this.groupName.hashCode();
        result = result * 31 + this.profilePictureUrl.hashCode();
        int n = this.useAnalytics ? 1 : 0;
        if (n != 0) {
            n = 1;
        }
        result = result * 31 + n;
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstanceUserGroupRestrictionDto)) {
            return false;
        }
        InstanceUserGroupRestrictionDto instanceUserGroupRestrictionDto = (InstanceUserGroupRestrictionDto)other;
        if (!Intrinsics.areEqual((Object)this.groupName, (Object)instanceUserGroupRestrictionDto.groupName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.profilePictureUrl, (Object)instanceUserGroupRestrictionDto.profilePictureUrl)) {
            return false;
        }
        return this.useAnalytics == instanceUserGroupRestrictionDto.useAnalytics;
    }
}

