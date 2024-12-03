/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.upm.servlet;

import com.atlassian.upm.UpmSys;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.servlet.UpmServletHandler;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.servlet.PluginManagerHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class PurchasedAddonsServlet
extends HttpServlet {
    private final PluginManagerHandler handler;
    private final PermissionEnforcer permissionEnforcer;
    private final HostLicenseProvider licenseProvider;

    public PurchasedAddonsServlet(PluginManagerHandler handler, PermissionEnforcer permissionEnforcer, HostLicenseProvider licenseProvider) {
        this.handler = Objects.requireNonNull(handler, "handler");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.licenseProvider = Objects.requireNonNull(licenseProvider, "licenseProvider");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!this.permissionEnforcer.hasPermission(Permission.GET_PURCHASED_PLUGINS)) {
            this.handler.redirectToLogin(request, response, UpmServletHandler.PermissionLevel.ADMIN);
            return;
        }
        this.handler.handle(request, response, "purchased-addons.vm", true, this.getAdditionalParams());
    }

    private Map<String, Object> getAdditionalParams() {
        HashMap<String, Object> additionalParams = new HashMap<String, Object>();
        for (Pair<String, Boolean> reason : PurchasedAddonsServlet.getPurchasedAddonsNonFunctionalReason(this.licenseProvider.getHostApplicationLicenseAttributes())) {
            if (reason.second().booleanValue()) {
                additionalParams.put("nonProdLicenseTypeKey", reason.first());
                continue;
            }
            additionalParams.put("genericWarningMessageKey", reason.first());
        }
        return additionalParams;
    }

    public static Option<Pair<String, Boolean>> getPurchasedAddonsNonFunctionalReason(HostApplicationLicenseAttributes license) {
        if (!UpmSys.isPurchasedAddonsEnabled()) {
            return Option.some(Pair.pair("upm.purchased.addons.feature.disabled", false));
        }
        if (license.isEvaluation()) {
            return Option.some(Pair.pair("upm.purchased.addons.nonprod.description.eval", true));
        }
        if (LicenseType.DEVELOPER.equals((Object)license.getLicenseType())) {
            return Option.some(Pair.pair("upm.purchased.addons.nonprod.description.dev", true));
        }
        if (LicenseType.TESTING.equals((Object)license.getLicenseType())) {
            return Option.some(Pair.pair("upm.purchased.addons.nonprod.description.test", true));
        }
        if (!license.getSen().isDefined()) {
            return Option.some(Pair.pair("upm.purchased.addons.nonprod.description.sen", true));
        }
        return Option.none();
    }
}

