/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.upm.api.license.PluginLicenseManager
 *  com.atlassian.upm.api.license.entity.LicenseError
 *  com.atlassian.upm.api.license.entity.PluginLicense
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.LicenseService;
import com.addonengine.addons.analytics.service.confluence.model.LicenseStatus;
import com.addonengine.addons.analytics.util.UtilsKt;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ExportAsDevService(value={LicenseService.class})
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001b\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\r\u001a\u00020\bH\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\bH\u0016J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/LicenseServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "pluginLicenseManager", "Lcom/atlassian/upm/api/license/PluginLicenseManager;", "productLicenseService", "Lcom/atlassian/confluence/license/LicenseService;", "(Lcom/atlassian/upm/api/license/PluginLicenseManager;Lcom/atlassian/confluence/license/LicenseService;)V", "statusOverride", "Lcom/addonengine/addons/analytics/service/confluence/model/LicenseStatus;", "getStatusOverride", "()Lcom/addonengine/addons/analytics/service/confluence/model/LicenseStatus;", "setStatusOverride", "(Lcom/addonengine/addons/analytics/service/confluence/model/LicenseStatus;)V", "getAppLicenseStatus", "getLicensedUserCount", "", "getStatus", "setStatusForTesting", "", "status", "analytics"})
public final class LicenseServiceServerImpl
implements LicenseService {
    @NotNull
    private final PluginLicenseManager pluginLicenseManager;
    @NotNull
    private final com.atlassian.confluence.license.LicenseService productLicenseService;
    @Nullable
    private LicenseStatus statusOverride;

    @Inject
    public LicenseServiceServerImpl(@ComponentImport @NotNull PluginLicenseManager pluginLicenseManager, @ComponentImport @NotNull com.atlassian.confluence.license.LicenseService productLicenseService) {
        Intrinsics.checkNotNullParameter((Object)pluginLicenseManager, (String)"pluginLicenseManager");
        Intrinsics.checkNotNullParameter((Object)productLicenseService, (String)"productLicenseService");
        this.pluginLicenseManager = pluginLicenseManager;
        this.productLicenseService = productLicenseService;
    }

    @Nullable
    public final LicenseStatus getStatusOverride() {
        return this.statusOverride;
    }

    public final void setStatusOverride(@Nullable LicenseStatus licenseStatus) {
        this.statusOverride = licenseStatus;
    }

    @Override
    @NotNull
    public LicenseStatus getStatus() {
        return this.productLicenseService.isLicensedForDataCenterOrExempt() ? LicenseStatus.VALID : this.getAppLicenseStatus();
    }

    private final LicenseStatus getAppLicenseStatus() {
        if (ConfluenceSystemProperties.isDevMode() && this.statusOverride != null) {
            LicenseStatus licenseStatus = this.statusOverride;
            Intrinsics.checkNotNull((Object)((Object)licenseStatus), (String)"null cannot be cast to non-null type com.addonengine.addons.analytics.service.confluence.model.LicenseStatus");
            return licenseStatus;
        }
        if (UtilsKt.isAddonDevMode()) {
            return LicenseStatus.VALID;
        }
        if (this.pluginLicenseManager.getLicense().isDefined()) {
            Object object = this.pluginLicenseManager.getLicense().get();
            Intrinsics.checkNotNull((Object)object);
            PluginLicense license = (PluginLicense)object;
            if (license.getError().isDefined()) {
                LicenseStatus licenseStatus;
                Object object2 = license.getError().get();
                Intrinsics.checkNotNull((Object)object2);
                switch (WhenMappings.$EnumSwitchMapping$0[((LicenseError)object2).ordinal()]) {
                    case 1: {
                        licenseStatus = LicenseStatus.EXPIRED;
                        break;
                    }
                    case 2: {
                        licenseStatus = LicenseStatus.TYPE_MISMATCH;
                        break;
                    }
                    case 3: {
                        licenseStatus = LicenseStatus.USER_MISMATCH;
                        break;
                    }
                    case 4: {
                        licenseStatus = LicenseStatus.EDITION_MISMATCH;
                        break;
                    }
                    case 5: {
                        licenseStatus = LicenseStatus.ROLE_EXCEEDED;
                        break;
                    }
                    case 6: {
                        licenseStatus = LicenseStatus.ROLE_UNDEFINED;
                        break;
                    }
                    case 7: {
                        licenseStatus = LicenseStatus.VERSION_MISMATCH;
                        break;
                    }
                    default: {
                        throw new NoWhenBranchMatchedException();
                    }
                }
                return licenseStatus;
            }
            return LicenseStatus.VALID;
        }
        return LicenseStatus.UNLICENSED;
    }

    @Override
    public int getLicensedUserCount() {
        if (this.productLicenseService.isLicensedForDataCenterOrExempt()) {
            return this.productLicenseService.retrieve().getMaximumNumberOfUsers();
        }
        Object object = this.pluginLicenseManager.getCurrentUserCountInLicenseRole().get();
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
        return ((Number)object).intValue();
    }

    @Override
    public void setStatusForTesting(@Nullable LicenseStatus status) {
        this.statusOverride = status;
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[LicenseError.values().length];
            try {
                nArray[LicenseError.EXPIRED.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.TYPE_MISMATCH.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.USER_MISMATCH.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.EDITION_MISMATCH.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.ROLE_EXCEEDED.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.ROLE_UNDEFINED.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseError.VERSION_MISMATCH.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

