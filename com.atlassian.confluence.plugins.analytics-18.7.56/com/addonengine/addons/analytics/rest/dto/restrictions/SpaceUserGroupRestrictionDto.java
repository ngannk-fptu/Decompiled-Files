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

import com.addonengine.addons.analytics.rest.dto.restrictions.SpaceRestrictionDto;
import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/SpaceUserGroupRestrictionDto;", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/SpaceRestrictionDto;", "groupName", "", "profilePictureUrl", "Ljava/net/URL;", "(Ljava/lang/String;Ljava/net/URL;)V", "getGroupName", "()Ljava/lang/String;", "getProfilePictureUrl", "()Ljava/net/URL;", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "analytics"})
public final class SpaceUserGroupRestrictionDto
implements SpaceRestrictionDto {
    @NotNull
    private final String groupName;
    @NotNull
    private final URL profilePictureUrl;

    public SpaceUserGroupRestrictionDto(@JsonProperty(value="groupName") @NotNull String groupName, @JsonProperty(value="profilePictureUrl") @NotNull URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)groupName, (String)"groupName");
        Intrinsics.checkNotNullParameter((Object)profilePictureUrl, (String)"profilePictureUrl");
        this.groupName = groupName;
        this.profilePictureUrl = profilePictureUrl;
    }

    @NotNull
    public final String getGroupName() {
        return this.groupName;
    }

    @NotNull
    public final URL getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final String component1() {
        return this.groupName;
    }

    @NotNull
    public final URL component2() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final SpaceUserGroupRestrictionDto copy(@JsonProperty(value="groupName") @NotNull String groupName, @JsonProperty(value="profilePictureUrl") @NotNull URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)groupName, (String)"groupName");
        Intrinsics.checkNotNullParameter((Object)profilePictureUrl, (String)"profilePictureUrl");
        return new SpaceUserGroupRestrictionDto(groupName, profilePictureUrl);
    }

    public static /* synthetic */ SpaceUserGroupRestrictionDto copy$default(SpaceUserGroupRestrictionDto spaceUserGroupRestrictionDto, String string, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceUserGroupRestrictionDto.groupName;
        }
        if ((n & 2) != 0) {
            uRL = spaceUserGroupRestrictionDto.profilePictureUrl;
        }
        return spaceUserGroupRestrictionDto.copy(string, uRL);
    }

    @NotNull
    public String toString() {
        return "SpaceUserGroupRestrictionDto(groupName=" + this.groupName + ", profilePictureUrl=" + this.profilePictureUrl + ')';
    }

    public int hashCode() {
        int result = this.groupName.hashCode();
        result = result * 31 + this.profilePictureUrl.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceUserGroupRestrictionDto)) {
            return false;
        }
        SpaceUserGroupRestrictionDto spaceUserGroupRestrictionDto = (SpaceUserGroupRestrictionDto)other;
        if (!Intrinsics.areEqual((Object)this.groupName, (Object)spaceUserGroupRestrictionDto.groupName)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.profilePictureUrl, (Object)spaceUserGroupRestrictionDto.profilePictureUrl);
    }
}

