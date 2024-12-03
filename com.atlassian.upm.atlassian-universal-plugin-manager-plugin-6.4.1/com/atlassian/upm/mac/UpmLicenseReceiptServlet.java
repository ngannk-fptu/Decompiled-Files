/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mac;

import com.atlassian.upm.analytics.PluginLicenseChangeAnalyticHelper;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.mac.LicenseReceiptHandler;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmLicenseReceiptServlet
extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(UpmLicenseReceiptServlet.class);
    private final UpmUriBuilder uriBuilder;
    private final AuditLogService auditLogService;
    private final PluginRetriever pluginRetriever;
    private final PluginLicenseRepository licenseRepository;
    private final LicenseReceiptHandler handler;
    private final PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper;
    public static final String MESSAGE_ERROR_PLUGIN_DISABLED = "upm.plugin.license.error.disabled.after.postback";
    public static final String MESSAGE_ERROR_INVALID_REQUEST = "upm.plugin.license.error.invalid.postback";
    public static final String MESSAGE_ERROR_NOT_LOGGED_IN = "upm.plugin.license.error.unauthenticated.postback";

    public UpmLicenseReceiptServlet(PluginRetriever pluginRetriever, PluginLicenseRepository licenseRepository, LicenseReceiptHandler handler, UpmUriBuilder uriBuilder, AuditLogService auditLogService, PluginLicenseChangeAnalyticHelper pluginLicenseChangeAnalyticHelper) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.auditLogService = Objects.requireNonNull(auditLogService, "auditLogService");
        this.pluginLicenseChangeAnalyticHelper = Objects.requireNonNull(pluginLicenseChangeAnalyticHelper, "pluginLicenseChangeAnalyticHelper");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pluginKey = request.getPathInfo().substring(request.getPathInfo().lastIndexOf(47) + 1);
        boolean licensePreviouslyDefined = this.licenseRepository.getPluginLicense(pluginKey).isDefined();
        boolean wasEnabled = this.pluginRetriever.isPluginEnabled(pluginKey);
        Either<LicenseReceiptHandler.ErrorResult, PluginLicense> result = this.handler.handle(request);
        if (result.isRight()) {
            Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
            if (iterator.hasNext()) {
                Plugin p = iterator.next();
                String logMessage = licensePreviouslyDefined ? "upm.auditLog.plugin.license.update" : "upm.auditLog.plugin.license.add";
                this.auditLogService.logI18nMessage(logMessage, p.getName(), pluginKey);
                this.pluginLicenseChangeAnalyticHelper.logPluginLicenseChanged(pluginKey);
                boolean isEnabled = this.pluginRetriever.isPluginEnabled(pluginKey);
                String messageCode = isEnabled ? "" : (wasEnabled ? MESSAGE_ERROR_PLUGIN_DISABLED : "");
                this.redirectToUpmPluginDetails(pluginKey, messageCode, response);
                return;
            }
            log.warn("Received a posted-back license for a plugin which is not currently installed: " + pluginKey);
            response.sendError(400);
            return;
        }
        for (LicenseReceiptHandler.ErrorResult error : result.left()) {
            if (error.isRedirectable()) {
                String messageCode = error == LicenseReceiptHandler.ErrorResult.NOT_AUTHENTICATED ? MESSAGE_ERROR_NOT_LOGGED_IN : MESSAGE_ERROR_INVALID_REQUEST;
                this.redirectToUpmPluginDetails(pluginKey, messageCode, response);
                continue;
            }
            response.sendError(400);
            return;
        }
    }

    private void redirectToUpmPluginDetails(String pluginKey, String messageCode, HttpServletResponse response) throws IOException {
        response.sendRedirect(this.uriBuilder.buildUpmTabPluginUri("manage", pluginKey, "licensed:" + messageCode).toASCIIString());
    }
}

