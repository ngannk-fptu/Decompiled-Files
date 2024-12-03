/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.johnson.event.Event;
import com.atlassian.spring.container.ContainerManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseValidationHealthCheck
extends HealthCheckTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseValidationHealthCheck.class);
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/confkb/startup-check-confluence-licensing-issues-935907159.html?utm_source=Install&utm_medium=in-product&utm_campaign=csseng_fy18_q2_server_confluence_errorstate");
    private final BootstrapManager bootstrapManager;
    private final LicenseService licenseService;
    private final LicenseValidator licenseValidator;

    public LicenseValidationHealthCheck(BootstrapManager bootstrapManager, LicenseService licenseService, LicenseValidator licenseValidator) {
        super(Collections.emptyList());
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.licenseService = Objects.requireNonNull(licenseService);
        this.licenseValidator = Objects.requireNonNull(licenseValidator);
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.BOOTSTRAP_END);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        if (this.bootstrapManager.isSetupComplete()) {
            try {
                ConfluenceLicense confluenceLicense = this.licenseService.retrieve();
                this.licenseValidator.validate(confluenceLicense);
            }
            catch (KnownConfluenceLicenseValidationException e) {
                LOGGER.error("License validation failed.");
                return this.toHealthCheckResult(e.reason().getReasonKey());
            }
            catch (LicenseException e) {
                LOGGER.error("Unable to retrieve license.");
                return this.toHealthCheckResult("license.invalid.upgrade.desc1.unknown");
            }
        }
        return Collections.emptyList();
    }

    @VisibleForTesting
    List<HealthCheckResult> toHealthCheckResult(String reasonKey) {
        HealthCheckMessage message = this.getHealthCheckMessage(reasonKey);
        Event event = new Event(this.getJohnsonEventType(reasonKey).eventType(), message.getHeadline(), message.asHtml(), JohnsonEventLevel.FATAL.level());
        return HealthCheckResult.fail(this, event, KB_URL, reasonKey, message.asText());
    }

    @VisibleForTesting
    HealthCheckMessage getHealthCheckMessage(String reasonKey) {
        return "error.license.legacy.server".equals(reasonKey) ? this.getHealthCheckMessageForCompatibility() : this.getHealthCheckMessageForInconsistency(reasonKey);
    }

    @VisibleForTesting
    HealthCheckMessage getHealthCheckMessageForInconsistency(String reasonKey) {
        I18NBean i18NBean = this.getI18n();
        return new HealthCheckMessage.Builder().withHeading(i18NBean.getText(reasonKey)).append("You'll need to supply a valid license key. Confluence can't start without this.").lineBreak().append("If you have a new license, please enter it on this ").appendLink(this.getFixLicenseUrl(), "page", false).append(" and restart.").build();
    }

    @VisibleForTesting
    HealthCheckMessage getHealthCheckMessageForCompatibility() {
        I18NBean i18NBean = this.getI18n();
        return new HealthCheckMessage.Builder().withHeading(i18NBean.getText("license.invalid.upgrade.desc1.server")).append(i18NBean.getText("license.invalid.upgrade.desc2.server") + " ").appendLink(this.getFixLicenseUrl(), i18NBean.getText("license.invalid.upgrade.desc3.server"), false).build();
    }

    private String getFixLicenseUrl() {
        return String.format("%s/fixonly/fixlicense.action", this.bootstrapManager.getWebAppContextPath());
    }

    @VisibleForTesting
    I18NBean getI18n() {
        return ((I18NBeanFactory)ContainerManager.getComponent((String)"i18NBeanFactory")).getI18NBean();
    }

    private JohnsonEventType getJohnsonEventType(String reasonKey) {
        return "error.license.legacy.server".equals(reasonKey) ? JohnsonEventType.LICENSE_INCOMPATIBLE : JohnsonEventType.LICENSE_INCONSISTENCY;
    }
}

