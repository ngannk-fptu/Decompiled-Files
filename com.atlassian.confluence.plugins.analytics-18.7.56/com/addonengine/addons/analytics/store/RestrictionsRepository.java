/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store;

import com.addonengine.addons.analytics.store.model.InstanceRestrictionsData;
import com.addonengine.addons.analytics.store.model.SpaceRestrictionsData;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0003H&J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0005H&\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/store/RestrictionsRepository;", "", "getInstanceRestrictions", "Lcom/addonengine/addons/analytics/store/model/InstanceRestrictionsData;", "getSpaceRestrictions", "Lcom/addonengine/addons/analytics/store/model/SpaceRestrictionsData;", "spaceKey", "", "saveInstanceRestrictions", "restrictions", "saveSpaceRestrictions", "analytics"})
public interface RestrictionsRepository {
    @NotNull
    public InstanceRestrictionsData getInstanceRestrictions();

    @NotNull
    public SpaceRestrictionsData getSpaceRestrictions(@NotNull String var1);

    @NotNull
    public InstanceRestrictionsData saveInstanceRestrictions(@NotNull InstanceRestrictionsData var1);

    @NotNull
    public SpaceRestrictionsData saveSpaceRestrictions(@NotNull String var1, @NotNull SpaceRestrictionsData var2);
}

