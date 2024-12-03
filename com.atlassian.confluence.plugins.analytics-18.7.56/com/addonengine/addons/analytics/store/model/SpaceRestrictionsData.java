/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.model;

import com.addonengine.addons.analytics.store.model.UserGroupRestrictionData;
import com.addonengine.addons.analytics.store.model.UserRestrictionData;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\u0002\u0010\u0007J\u000f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J)\u0010\r\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\t\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/store/model/SpaceRestrictionsData;", "", "userGroups", "", "Lcom/addonengine/addons/analytics/store/model/UserGroupRestrictionData;", "users", "Lcom/addonengine/addons/analytics/store/model/UserRestrictionData;", "(Ljava/util/List;Ljava/util/List;)V", "getUserGroups", "()Ljava/util/List;", "getUsers", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class SpaceRestrictionsData {
    @NotNull
    private final List<UserGroupRestrictionData> userGroups;
    @NotNull
    private final List<UserRestrictionData> users;

    public SpaceRestrictionsData(@NotNull List<UserGroupRestrictionData> userGroups, @NotNull List<UserRestrictionData> users) {
        Intrinsics.checkNotNullParameter(userGroups, (String)"userGroups");
        Intrinsics.checkNotNullParameter(users, (String)"users");
        this.userGroups = userGroups;
        this.users = users;
    }

    @NotNull
    public final List<UserGroupRestrictionData> getUserGroups() {
        return this.userGroups;
    }

    @NotNull
    public final List<UserRestrictionData> getUsers() {
        return this.users;
    }

    @NotNull
    public final List<UserGroupRestrictionData> component1() {
        return this.userGroups;
    }

    @NotNull
    public final List<UserRestrictionData> component2() {
        return this.users;
    }

    @NotNull
    public final SpaceRestrictionsData copy(@NotNull List<UserGroupRestrictionData> userGroups, @NotNull List<UserRestrictionData> users) {
        Intrinsics.checkNotNullParameter(userGroups, (String)"userGroups");
        Intrinsics.checkNotNullParameter(users, (String)"users");
        return new SpaceRestrictionsData(userGroups, users);
    }

    public static /* synthetic */ SpaceRestrictionsData copy$default(SpaceRestrictionsData spaceRestrictionsData, List list, List list2, int n, Object object) {
        if ((n & 1) != 0) {
            list = spaceRestrictionsData.userGroups;
        }
        if ((n & 2) != 0) {
            list2 = spaceRestrictionsData.users;
        }
        return spaceRestrictionsData.copy(list, list2);
    }

    @NotNull
    public String toString() {
        return "SpaceRestrictionsData(userGroups=" + this.userGroups + ", users=" + this.users + ')';
    }

    public int hashCode() {
        int result = ((Object)this.userGroups).hashCode();
        result = result * 31 + ((Object)this.users).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceRestrictionsData)) {
            return false;
        }
        SpaceRestrictionsData spaceRestrictionsData = (SpaceRestrictionsData)other;
        if (!Intrinsics.areEqual(this.userGroups, spaceRestrictionsData.userGroups)) {
            return false;
        }
        return Intrinsics.areEqual(this.users, spaceRestrictionsData.users);
    }
}

