/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.restrictions.InstanceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.SpaceRestrictions;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH&J\u0010\u0010\r\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H&J\u0018\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0003H&J,\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013H&\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/service/RestrictionsService;", "", "getInstanceRestrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/InstanceRestrictions;", "getSpaceRestrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/SpaceRestrictions;", "spaceKey", "", "isUserAllowedToViewContentAnalytics", "", "userKey", "contentId", "", "isUserAllowedToViewInstanceAnalytics", "isUserAllowedToViewSpaceAnalytics", "saveInstanceRestrictions", "restrictions", "saveSpaceRestrictions", "userRestrictions", "", "userGroupRestrictions", "analytics"})
public interface RestrictionsService {
    @NotNull
    public InstanceRestrictions getInstanceRestrictions();

    @NotNull
    public SpaceRestrictions getSpaceRestrictions(@NotNull String var1);

    @NotNull
    public InstanceRestrictions saveInstanceRestrictions(@NotNull InstanceRestrictions var1);

    @NotNull
    public SpaceRestrictions saveSpaceRestrictions(@NotNull String var1, @NotNull List<String> var2, @NotNull List<String> var3);

    public boolean isUserAllowedToViewInstanceAnalytics(@NotNull String var1);

    public boolean isUserAllowedToViewSpaceAnalytics(@NotNull String var1, @NotNull String var2);

    public boolean isUserAllowedToViewContentAnalytics(@NotNull String var1, long var2);
}

