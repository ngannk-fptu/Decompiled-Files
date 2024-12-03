/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license.exception.handler;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.exception.handler.AbstractLicenseExceptionHandler;
import com.atlassian.confluence.license.exception.handler.CompositeLicenseExceptionHandler;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.base.Preconditions;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnownConfluenceLicenseValidationExceptionHandler
extends AbstractLicenseExceptionHandler<KnownConfluenceLicenseValidationException> {
    public static final Logger log = LoggerFactory.getLogger(CompositeLicenseExceptionHandler.class);
    private final DocumentationBean docBean;
    private final ConfluenceSidManager sidManager;

    public KnownConfluenceLicenseValidationExceptionHandler(I18NBean i18n, DocumentationBean docBean, ConfluenceSidManager sidManager) {
        super(i18n);
        this.docBean = (DocumentationBean)Preconditions.checkNotNull((Object)docBean);
        this.sidManager = (ConfluenceSidManager)Preconditions.checkNotNull((Object)sidManager);
    }

    @Override
    public String handle(KnownConfluenceLicenseValidationException exception) {
        switch (exception.reason()) {
            case SUPPORT_EXPIRED: {
                return this.createMessageWithDate("error.license.too.old", exception.getLicense().getMaintenanceExpiryDate());
            }
            case LEGACY_VERSION_1: {
                return this.createLegacyVersion1Message();
            }
            case LEGACY_CLUSTER_LICENSE: {
                return this.lookupMessage("error.license.legacy.cluster", new Object[0]);
            }
            case LICENCE_NOT_FOR_CDC: {
                return this.lookupMessage("error.license.cdc.not.licensed", new Object[0]);
            }
            case LICENCE_NOT_FOR_STANDALONE: {
                return this.lookupMessage("error.license.standalone.not.licensed", new Object[0]);
            }
            case LICENSE_EXPIRY_MISSING: {
                return this.lookupMessage("error.license.cdc.expiry.missing", new Object[0]);
            }
            case LICENSE_INVALID_NUMBER_OF_USERS: {
                return this.lookupMessage("error.license.cdc.invalid.number.of.users", new Object[0]);
            }
            case LEGACY_SERVER_LICENSE: {
                return this.lookupMessage("error.license.legacy.server", new Object[0]);
            }
        }
        log.error("Passed an unknown reason: '{}'.", (Object)exception.reason());
        return null;
    }

    private String createMessageWithDate(@NonNull String key, @NonNull Date date) {
        String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(date);
        return this.lookupMessage(key, formattedDate);
    }

    private String createLegacyVersion1Message() {
        String macUrl = this.lookupMessage("url.my.atlassian", new Object[0]);
        String docUrl = this.calculateDocumentationUrl("help.url.v2.license.upgrade");
        String evalUrl = this.createLicenseEvaluationUrl();
        StringBuilder sb = new StringBuilder();
        sb.append(this.lookupMessage("license.invalid.v1.not.supported.desc1", new Object[0]));
        sb.append("<p>").append(this.lookupMessage("license.invalid.v1.not.supported.desc2", macUrl, docUrl)).append("</p>");
        sb.append("<p>").append(this.lookupMessage("license.invalid.v1.not.supported.desc3", evalUrl)).append("</p>");
        return sb.toString();
    }

    private String createLicenseEvaluationUrl() {
        String versionNumber = BuildInformation.INSTANCE.getVersionNumber();
        String buildNumber = BuildInformation.INSTANCE.getBuildNumber();
        return this.lookupMessage("url.atlassian.confluence.generate.eval.license", versionNumber, buildNumber, this.currentServerId(), "");
    }

    private String calculateDocumentationUrl(String key) {
        return this.docBean.getLink(key);
    }

    private String currentServerId() {
        String serverId = null;
        try {
            serverId = this.sidManager.getSid();
        }
        catch (ConfigurationException e) {
            log.error("Could not retrieve the server identifier (SID), see cause.", (Throwable)e);
        }
        if (StringUtils.isBlank((CharSequence)serverId)) {
            serverId = "";
        }
        return serverId;
    }
}

