/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence.model;

import com.addonengine.addons.analytics.service.confluence.model.UserType;
import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\tH\u00c6\u0003JA\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006\u001f"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/User;", "", "type", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "userKey", "", "displayName", "email", "profilePictureUrl", "Ljava/net/URL;", "(Lcom/addonengine/addons/analytics/service/confluence/model/UserType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/net/URL;)V", "getDisplayName", "()Ljava/lang/String;", "getEmail", "getProfilePictureUrl", "()Ljava/net/URL;", "getType", "()Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getUserKey", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class User {
    @NotNull
    private final UserType type;
    @Nullable
    private final String userKey;
    @NotNull
    private final String displayName;
    @Nullable
    private final String email;
    @Nullable
    private final URL profilePictureUrl;

    public User(@NotNull UserType type, @Nullable String userKey, @NotNull String displayName, @Nullable String email, @Nullable URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        this.type = type;
        this.userKey = userKey;
        this.displayName = displayName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    @NotNull
    public final UserType getType() {
        return this.type;
    }

    @Nullable
    public final String getUserKey() {
        return this.userKey;
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
    public final URL getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final UserType component1() {
        return this.type;
    }

    @Nullable
    public final String component2() {
        return this.userKey;
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
    public final URL component5() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final User copy(@NotNull UserType type, @Nullable String userKey, @NotNull String displayName, @Nullable String email, @Nullable URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        return new User(type, userKey, displayName, email, profilePictureUrl);
    }

    public static /* synthetic */ User copy$default(User user, UserType userType, String string, String string2, String string3, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            userType = user.type;
        }
        if ((n & 2) != 0) {
            string = user.userKey;
        }
        if ((n & 4) != 0) {
            string2 = user.displayName;
        }
        if ((n & 8) != 0) {
            string3 = user.email;
        }
        if ((n & 0x10) != 0) {
            uRL = user.profilePictureUrl;
        }
        return user.copy(userType, string, string2, string3, uRL);
    }

    @NotNull
    public String toString() {
        return "User(type=" + (Object)((Object)this.type) + ", userKey=" + this.userKey + ", displayName=" + this.displayName + ", email=" + this.email + ", profilePictureUrl=" + this.profilePictureUrl + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + (this.userKey == null ? 0 : this.userKey.hashCode());
        result = result * 31 + this.displayName.hashCode();
        result = result * 31 + (this.email == null ? 0 : this.email.hashCode());
        result = result * 31 + (this.profilePictureUrl == null ? 0 : this.profilePictureUrl.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }
        User user = (User)other;
        if (this.type != user.type) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userKey, (Object)user.userKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.displayName, (Object)user.displayName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.email, (Object)user.email)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.profilePictureUrl, (Object)user.profilePictureUrl);
    }
}

