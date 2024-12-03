/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.settings.model;

import com.addonengine.addons.analytics.store.server.settings.model.UserGroupRestriction;
import com.addonengine.addons.analytics.store.server.settings.model.UserRestriction;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B%\u0012\u000e\b\u0001\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0001\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\u0002\u0010\u0007R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\t\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/model/SpaceRestrictionsSetting;", "", "userGroups", "", "Lcom/addonengine/addons/analytics/store/server/settings/model/UserGroupRestriction;", "users", "Lcom/addonengine/addons/analytics/store/server/settings/model/UserRestriction;", "(Ljava/util/List;Ljava/util/List;)V", "getUserGroups", "()Ljava/util/List;", "getUsers", "analytics"})
public final class SpaceRestrictionsSetting {
    @NotNull
    private final List<UserGroupRestriction> userGroups;
    @NotNull
    private final List<UserRestriction> users;

    public SpaceRestrictionsSetting(@JsonProperty(value="userGroups") @NotNull List<UserGroupRestriction> userGroups, @JsonProperty(value="users") @NotNull List<UserRestriction> users) {
        Intrinsics.checkNotNullParameter(userGroups, (String)"userGroups");
        Intrinsics.checkNotNullParameter(users, (String)"users");
        this.userGroups = userGroups;
        this.users = users;
    }

    @NotNull
    public final List<UserGroupRestriction> getUserGroups() {
        return this.userGroups;
    }

    @NotNull
    public final List<UserRestriction> getUsers() {
        return this.users;
    }
}

