/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.mac;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.license.internal.mac.LicenseReceiptValidator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseReceiptHandler {
    private static final Logger log = LoggerFactory.getLogger(LicenseReceiptHandler.class);
    private final UpmPluginAccessor pluginAccessor;
    private final UserManager userManager;
    private final PluginLicenseValidator pluginLicenseValidator;
    private final PluginLicenseRepository pluginLicenseRepository;
    private final LicenseReceiptValidator licenseReceiptValidator;
    public static final String LICENSE_PARAM = "license";

    public LicenseReceiptHandler(UpmPluginAccessor pluginAccessor, UserManager userManager, PluginLicenseValidator pluginLicenseValidator, PluginLicenseRepository pluginLicenseRepository, LicenseReceiptValidator licenseReceiptValidator) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.pluginLicenseValidator = Objects.requireNonNull(pluginLicenseValidator, "pluginLicenseValidator");
        this.pluginLicenseRepository = Objects.requireNonNull(pluginLicenseRepository, "pluginLicenseRepository");
        this.licenseReceiptValidator = Objects.requireNonNull(licenseReceiptValidator, "licenseReceiptValidator");
    }

    public Either<ErrorResult, PluginLicense> handle(HttpServletRequest request) {
        String pluginKey = request.getPathInfo().substring(request.getPathInfo().lastIndexOf(47) + 1);
        if (StringUtils.isBlank((CharSequence)pluginKey)) {
            return Either.left(ErrorResult.BAD_REQUEST);
        }
        if (!this.pluginAccessor.getPlugin(pluginKey).isDefined()) {
            return Either.left(ErrorResult.BAD_REQUEST);
        }
        String licenseString = request.getParameter(LICENSE_PARAM);
        if (StringUtils.isBlank((CharSequence)licenseString)) {
            return Either.left(ErrorResult.BAD_REQUEST);
        }
        if (!this.hasPermission()) {
            log.warn("Unable to store new license for \"" + pluginKey + "\": not logged in as an administrator");
            return Either.left(ErrorResult.NOT_AUTHENTICATED);
        }
        String referrer = request.getHeader("Referer");
        if (referrer != null && !this.isFromMacDomain(referrer)) {
            log.warn("Refused a request that had an unexpected referrer: " + referrer);
            return Either.left(ErrorResult.BAD_REFERRER);
        }
        Either<PluginLicenseError, PluginLicense> maybeLicense = this.pluginLicenseValidator.validate(pluginKey, licenseString);
        Iterator<Object> iterator = maybeLicense.left().iterator();
        if (iterator.hasNext()) {
            PluginLicenseError error = iterator.next();
            this.logRejection(pluginKey, "Could not decode license: " + error.getType().getSubCode());
            return Either.left(ErrorResult.MALFORMED_LICENSE);
        }
        iterator = maybeLicense.right().iterator();
        if (iterator.hasNext()) {
            PluginLicense license = (PluginLicense)iterator.next();
            Iterator<Enum> iterator2 = license.getError().iterator();
            if (iterator2.hasNext()) {
                LicenseError error = iterator2.next();
                this.logRejection(pluginKey, "License validation error: " + (Object)((Object)error));
                return Either.left(ErrorResult.UNACCEPTABLE_LICENSE);
            }
            iterator2 = this.licenseReceiptValidator.validateReceivedLicense(license, pluginKey).iterator();
            if (iterator2.hasNext()) {
                LicenseReceiptValidator.ValidationError error = (LicenseReceiptValidator.ValidationError)iterator2.next();
                this.logRejection(pluginKey, "License validation error: " + (Object)((Object)error));
                return Either.left(ErrorResult.UNACCEPTABLE_LICENSE);
            }
            this.pluginLicenseRepository.setPluginLicense(pluginKey, license.getRawLicense());
            return Either.right(license);
        }
        return Either.left(ErrorResult.BAD_REQUEST);
    }

    private boolean isFromMacDomain(String uriString) {
        String macBaseUrlSysProp = System.getProperty("mac.baseurl");
        String macBaseUrl = macBaseUrlSysProp != null ? macBaseUrlSysProp : "https://my.atlassian.com";
        try {
            URI uri = new URI(uriString);
            URI macUri = new URI(macBaseUrl);
            return uri.getHost().equals(macUri.getHost());
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean hasPermission() {
        UserKey loggedInUser = this.userManager.getRemoteUserKey();
        if (loggedInUser == null) {
            return false;
        }
        return this.userManager.isSystemAdmin(loggedInUser) || this.userManager.isAdmin(loggedInUser);
    }

    private void logRejection(String pluginKey, String errorDescription) {
        log.warn("Unable to store new license for \"" + pluginKey + "\": " + errorDescription);
    }

    public static enum ErrorResult {
        BAD_REQUEST(false),
        BAD_REFERRER(true),
        NOT_AUTHENTICATED(true),
        MALFORMED_LICENSE(true),
        UNACCEPTABLE_LICENSE(true);

        private final boolean redirectable;

        private ErrorResult(boolean redirectable) {
            this.redirectable = redirectable;
        }

        public boolean isRedirectable() {
            return this.redirectable;
        }
    }
}

