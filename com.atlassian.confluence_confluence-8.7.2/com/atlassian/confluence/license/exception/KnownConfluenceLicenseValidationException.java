/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.ConfluenceLicenseValidationException;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class KnownConfluenceLicenseValidationException
extends ConfluenceLicenseValidationException {
    public static final String LICENSE_INVALID_UPGRADE_UNKNOWN = "license.invalid.upgrade.desc1.unknown";
    public static final String LICENSE_INVALID_UPGRADE_LEGACY_SERVER = "error.license.legacy.server";
    private final Reason reason;

    public KnownConfluenceLicenseValidationException(ConfluenceLicense license, Reason reason) {
        this(String.format("Confluence license [%s] is invalid, reason [%s].", ToStringBuilder.reflectionToString((Object)license), reason.name()), license, reason);
    }

    public KnownConfluenceLicenseValidationException(String message, ConfluenceLicense license, Reason reason) {
        super(message, license);
        this.reason = reason;
    }

    public Reason reason() {
        return this.reason;
    }

    public static enum Reason {
        SUPPORT_EXPIRED("license.invalid.upgrade.desc1.support"),
        LEGACY_VERSION_1("license.invalid.upgrade.desc1.unknown"),
        LEGACY_CLUSTER_LICENSE("license.invalid.upgrade.desc1.legacy"),
        LICENCE_NOT_FOR_CDC("license.invalid.upgrade.desc1.not.cdc"),
        LICENCE_NOT_FOR_STANDALONE("license.invalid.upgrade.desc1.not.standalone"),
        LICENSE_EXPIRY_MISSING("license.invalid.upgrade.desc1.unknown"),
        LICENSE_INVALID_NUMBER_OF_USERS("license.invalid.upgrade.desc1.unknown"),
        LEGACY_SERVER_LICENSE("error.license.legacy.server");

        private final String reasonKey;

        private Reason(String reasonKey) {
            this.reasonKey = reasonKey;
        }

        public String getReasonKey() {
            return this.reasonKey;
        }
    }
}

