/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model.restrictions;

import com.addonengine.addons.analytics.service.confluence.model.Group;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/service/model/restrictions/UserGroupRestriction;", "", "group", "Lcom/addonengine/addons/analytics/service/confluence/model/Group;", "useAnalytics", "", "(Lcom/addonengine/addons/analytics/service/confluence/model/Group;Z)V", "getGroup", "()Lcom/addonengine/addons/analytics/service/confluence/model/Group;", "getUseAnalytics", "()Z", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "analytics"})
public final class UserGroupRestriction {
    @NotNull
    private final Group group;
    private final boolean useAnalytics;

    public UserGroupRestriction(@NotNull Group group, boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)group, (String)"group");
        this.group = group;
        this.useAnalytics = useAnalytics;
    }

    @NotNull
    public final Group getGroup() {
        return this.group;
    }

    public final boolean getUseAnalytics() {
        return this.useAnalytics;
    }

    @NotNull
    public final Group component1() {
        return this.group;
    }

    public final boolean component2() {
        return this.useAnalytics;
    }

    @NotNull
    public final UserGroupRestriction copy(@NotNull Group group, boolean useAnalytics) {
        Intrinsics.checkNotNullParameter((Object)group, (String)"group");
        return new UserGroupRestriction(group, useAnalytics);
    }

    public static /* synthetic */ UserGroupRestriction copy$default(UserGroupRestriction userGroupRestriction, Group group, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            group = userGroupRestriction.group;
        }
        if ((n & 2) != 0) {
            bl = userGroupRestriction.useAnalytics;
        }
        return userGroupRestriction.copy(group, bl);
    }

    @NotNull
    public String toString() {
        return "UserGroupRestriction(group=" + this.group + ", useAnalytics=" + this.useAnalytics + ')';
    }

    public int hashCode() {
        int result = this.group.hashCode();
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
        if (!(other instanceof UserGroupRestriction)) {
            return false;
        }
        UserGroupRestriction userGroupRestriction = (UserGroupRestriction)other;
        if (!Intrinsics.areEqual((Object)this.group, (Object)userGroupRestriction.group)) {
            return false;
        }
        return this.useAnalytics == userGroupRestriction.useAnalytics;
    }
}

