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

import com.addonengine.addons.analytics.service.confluence.model.UserType;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003JA\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001d"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/UserDto;", "", "type", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "userId", "", "displayName", "email", "profilePictureUrl", "(Lcom/addonengine/addons/analytics/service/confluence/model/UserType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDisplayName", "()Ljava/lang/String;", "getEmail", "getProfilePictureUrl", "getType", "()Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getUserId", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class UserDto {
    @NotNull
    private final UserType type;
    @Nullable
    private final String userId;
    @NotNull
    private final String displayName;
    @Nullable
    private final String email;
    @Nullable
    private final String profilePictureUrl;

    public UserDto(@NotNull UserType type, @Nullable String userId, @NotNull String displayName, @Nullable String email, @Nullable String profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        this.type = type;
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    @NotNull
    public final UserType getType() {
        return this.type;
    }

    @Nullable
    public final String getUserId() {
        return this.userId;
    }

    @NotNull
    public final String getDisplayName() {
        return this.displayName;
    }

    @Nullable
    public final String getEmail() {
        return this.email;
    }

    @Nullable
    public final String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final UserType component1() {
        return this.type;
    }

    @Nullable
    public final String component2() {
        return this.userId;
    }

    @NotNull
    public final String component3() {
        return this.displayName;
    }

    @Nullable
    public final String component4() {
        return this.email;
    }

    @Nullable
    public final String component5() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final UserDto copy(@NotNull UserType type, @Nullable String userId, @NotNull String displayName, @Nullable String email, @Nullable String profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        return new UserDto(type, userId, displayName, email, profilePictureUrl);
    }

    public static /* synthetic */ UserDto copy$default(UserDto userDto, UserType userType, String string, String string2, String string3, String string4, int n, Object object) {
        if ((n & 1) != 0) {
            userType = userDto.type;
        }
        if ((n & 2) != 0) {
            string = userDto.userId;
        }
        if ((n & 4) != 0) {
            string2 = userDto.displayName;
        }
        if ((n & 8) != 0) {
            string3 = userDto.email;
        }
        if ((n & 0x10) != 0) {
            string4 = userDto.profilePictureUrl;
        }
        return userDto.copy(userType, string, string2, string3, string4);
    }

    @NotNull
    public String toString() {
        return "UserDto(type=" + (Object)((Object)this.type) + ", userId=" + this.userId + ", displayName=" + this.displayName + ", email=" + this.email + ", profilePictureUrl=" + this.profilePictureUrl + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + (this.userId == null ? 0 : this.userId.hashCode());
        result = result * 31 + this.displayName.hashCode();
        result = result * 31 + (this.email == null ? 0 : this.email.hashCode());
        result = result * 31 + (this.profilePictureUrl == null ? 0 : this.profilePictureUrl.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserDto)) {
            return false;
        }
        UserDto userDto = (UserDto)other;
        if (this.type != userDto.type) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userId, (Object)userDto.userId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.displayName, (Object)userDto.displayName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.email, (Object)userDto.email)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.profilePictureUrl, (Object)userDto.profilePictureUrl);
    }
}

