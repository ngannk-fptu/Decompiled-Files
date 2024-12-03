/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.applinks.internal.status.error.ApplinkErrorCategory;
import java.io.Serializable;
import javax.annotation.Nonnull;

public enum ApplinkErrorType {
    UNKNOWN(ApplinkErrorCategory.UNKNOWN),
    NO_OUTGOING_AUTH(ApplinkErrorCategory.INCOMPATIBLE),
    REMOTE_VERSION_INCOMPATIBLE(ApplinkErrorCategory.INCOMPATIBLE),
    GENERIC_LINK(ApplinkErrorCategory.NON_ATLASSIAN),
    NON_ATLASSIAN(ApplinkErrorCategory.NON_ATLASSIAN),
    SYSTEM_LINK(ApplinkErrorCategory.SYSTEM),
    CONNECTION_REFUSED(ApplinkErrorCategory.NETWORK_ERROR),
    UNKNOWN_HOST(ApplinkErrorCategory.NETWORK_ERROR),
    SSL_UNTRUSTED(ApplinkErrorCategory.NETWORK_ERROR),
    SSL_HOSTNAME_UNMATCHED(ApplinkErrorCategory.NETWORK_ERROR),
    SSL_UNMATCHED(ApplinkErrorCategory.NETWORK_ERROR),
    OAUTH_PROBLEM(ApplinkErrorCategory.NETWORK_ERROR),
    OAUTH_TIMESTAMP_REFUSED(ApplinkErrorCategory.NETWORK_ERROR),
    OAUTH_SIGNATURE_INVALID(ApplinkErrorCategory.NETWORK_ERROR),
    UNEXPECTED_RESPONSE(ApplinkErrorCategory.NETWORK_ERROR),
    UNEXPECTED_RESPONSE_STATUS(ApplinkErrorCategory.NETWORK_ERROR),
    LOCAL_AUTH_TOKEN_REQUIRED(ApplinkErrorCategory.ACCESS_ERROR),
    REMOTE_AUTH_TOKEN_REQUIRED(ApplinkErrorCategory.ACCESS_ERROR),
    INSUFFICIENT_REMOTE_PERMISSION(ApplinkErrorCategory.ACCESS_ERROR),
    NO_REMOTE_APPLINK(ApplinkErrorCategory.CONFIG_ERROR),
    AUTH_LEVEL_MISMATCH(ApplinkErrorCategory.CONFIG_ERROR),
    AUTH_LEVEL_UNSUPPORTED(ApplinkErrorCategory.DEPRECATED),
    LEGACY_UPDATE(ApplinkErrorCategory.DEPRECATED),
    LEGACY_REMOVAL(ApplinkErrorCategory.DEPRECATED),
    MANUAL_LEGACY_UPDATE(ApplinkErrorCategory.DEPRECATED),
    MANUAL_LEGACY_REMOVAL(ApplinkErrorCategory.DEPRECATED),
    MANUAL_LEGACY_REMOVAL_WITH_OLD_EDIT(ApplinkErrorCategory.DEPRECATED),
    DISABLED(ApplinkErrorCategory.DISABLED);

    private static final String I18N_TEMPLATE = "applinks.status.%s.title";
    private final ApplinkErrorCategory category;

    private ApplinkErrorType(ApplinkErrorCategory category) {
        this.category = category;
    }

    @Nonnull
    public ApplinkErrorCategory getCategory() {
        return this.category;
    }

    @Nonnull
    public I18nKey getI18nKey() {
        return I18nKey.newI18nKey(String.format(I18N_TEMPLATE, this.name().toLowerCase()), new Serializable[0]);
    }
}

