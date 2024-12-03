/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.context;

import com.addonengine.addons.analytics.service.confluence.LicenseService;
import com.addonengine.addons.analytics.service.confluence.model.LicenseStatus;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R%\u0010\u0007\u001a\u0016\u0012\u0004\u0012\u00020\t\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00010\n0\b8F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "", "pluginAccessor", "Lcom/atlassian/plugin/PluginAccessor;", "licenseService", "Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "(Lcom/atlassian/plugin/PluginAccessor;Lcom/addonengine/addons/analytics/service/confluence/LicenseService;)V", "errorMessageDetails", "Lkotlin/Pair;", "", "", "getErrorMessageDetails", "()Lkotlin/Pair;", "isValid", "", "()Z", "getLicenseService", "()Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "analytics"})
@SourceDebugExtension(value={"SMAP\nLicenseContext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LicenseContext.kt\ncom/addonengine/addons/analytics/web/context/LicenseContext\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,39:1\n26#2:40\n26#2:41\n26#2:42\n26#2:43\n26#2:44\n26#2:45\n26#2:46\n*S KotlinDebug\n*F\n+ 1 LicenseContext.kt\ncom/addonengine/addons/analytics/web/context/LicenseContext\n*L\n20#1:40\n21#1:41\n22#1:42\n23#1:43\n28#1:44\n29#1:45\n30#1:46\n*E\n"})
public final class LicenseContext {
    @NotNull
    private final PluginAccessor pluginAccessor;
    @NotNull
    private final LicenseService licenseService;

    @Inject
    public LicenseContext(@ComponentImport @NotNull PluginAccessor pluginAccessor, @NotNull LicenseService licenseService) {
        Intrinsics.checkNotNullParameter((Object)pluginAccessor, (String)"pluginAccessor");
        Intrinsics.checkNotNullParameter((Object)licenseService, (String)"licenseService");
        this.pluginAccessor = pluginAccessor;
        this.licenseService = licenseService;
    }

    @NotNull
    public final LicenseService getLicenseService() {
        return this.licenseService;
    }

    @NotNull
    public final Pair<String, Object[]> getErrorMessageDetails() {
        Pair pair;
        String addonKey = "com.addonengine.analytics";
        switch (WhenMappings.$EnumSwitchMapping$0[this.licenseService.getStatus().ordinal()]) {
            case 1: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"", (Object)new Object[0]);
                break;
            }
            case 2: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.unlicensed", (Object)new Object[0]);
                break;
            }
            case 3: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.expired", (Object)new Object[0]);
                break;
            }
            case 4: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.typeMismatch", (Object)new Object[0]);
                break;
            }
            case 5: {
                int licensedUserCount = this.licenseService.getLicensedUserCount();
                Integer[] integerArray = new Integer[]{licensedUserCount};
                Pair pair2 = new Pair((Object)"com.addonengine.addons.analytics.license.userMismatch", (Object)integerArray);
                pair = pair2;
                break;
            }
            case 6: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.editionMismatch", (Object)new Object[0]);
                break;
            }
            case 7: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.roleExceeded", (Object)new Object[0]);
                break;
            }
            case 8: {
                boolean $i$f$emptyArray = false;
                pair = new Pair((Object)"com.addonengine.addons.analytics.license.roleUndefined", (Object)new Object[0]);
                break;
            }
            case 9: {
                String version = this.pluginAccessor.getPlugin(addonKey).getPluginInformation().getVersion();
                String[] stringArray = new String[]{version};
                Pair pair3 = new Pair((Object)"com.addonengine.addons.analytics.license.versionMismatch", (Object)stringArray);
                pair = pair3;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return pair;
    }

    public final boolean isValid() {
        return this.licenseService.getStatus() == LicenseStatus.VALID;
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[LicenseStatus.values().length];
            try {
                nArray[LicenseStatus.VALID.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.UNLICENSED.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.EXPIRED.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.TYPE_MISMATCH.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.USER_MISMATCH.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.EDITION_MISMATCH.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.ROLE_EXCEEDED.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.ROLE_UNDEFINED.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[LicenseStatus.VERSION_MISMATCH.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

