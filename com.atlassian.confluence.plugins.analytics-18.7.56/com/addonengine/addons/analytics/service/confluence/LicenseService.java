/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence;

import com.addonengine.addons.analytics.service.confluence.model.LicenseStatus;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0005H&\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "", "getLicensedUserCount", "", "getStatus", "Lcom/addonengine/addons/analytics/service/confluence/model/LicenseStatus;", "setStatusForTesting", "", "status", "analytics"})
public interface LicenseService {
    @NotNull
    public LicenseStatus getStatus();

    public int getLicensedUserCount();

    public void setStatusForTesting(@Nullable LicenseStatus var1);
}

