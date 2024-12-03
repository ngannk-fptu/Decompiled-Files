/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.web.condition;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\u00020\u00062\u0014\u0010\u0007\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t\u0018\u00010\bH\u0016J\u001c\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000e0\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/web/condition/UserIsLoggedInCondition;", "Lcom/atlassian/plugin/web/Condition;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "(Lcom/atlassian/sal/api/user/UserManager;)V", "init", "", "params", "", "", "shouldDisplay", "", "context", "", "", "analytics"})
public final class UserIsLoggedInCondition
implements Condition {
    @NotNull
    private final UserManager userManager;

    @Inject
    public UserIsLoggedInCondition(@NotNull UserManager userManager) {
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        this.userManager = userManager;
    }

    public void init(@Nullable Map<String, String> params) {
    }

    public boolean shouldDisplay(@NotNull Map<String, ? extends Object> context) {
        Intrinsics.checkNotNullParameter(context, (String)"context");
        return this.userManager.getRemoteUser() != null;
    }
}

