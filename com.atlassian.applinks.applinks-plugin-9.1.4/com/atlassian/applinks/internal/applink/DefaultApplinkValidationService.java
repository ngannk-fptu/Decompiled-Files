/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.applink;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.applink.ApplinkValidationService;
import com.atlassian.applinks.internal.common.applink.ApplicationLinks;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ValidationException;
import com.atlassian.applinks.internal.common.exception.ValidationExceptionBuilder;
import com.atlassian.applinks.internal.common.lang.ApplinksStreams;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultApplinkValidationService
implements ApplinkValidationService {
    private final ApplinkHelper applinkHelper;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final I18nResolver i18nResolver;

    @Autowired
    public DefaultApplinkValidationService(ApplinkHelper applinkHelper, ReadOnlyApplicationLinkService applicationLinkService, I18nResolver i18nResolver) {
        this.applinkHelper = applinkHelper;
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public void validateUpdate(@Nonnull ApplicationId applicationId, @Nonnull ApplicationLinkDetails details) throws NoSuchApplinkException, ValidationException {
        this.validateUpdate(this.applinkHelper.getReadOnlyApplicationLink(applicationId), details);
    }

    @Override
    public void validateUpdate(@Nonnull ReadOnlyApplicationLink applink, @Nonnull ApplicationLinkDetails details) throws ValidationException {
        Objects.requireNonNull(applink, "applink");
        Objects.requireNonNull(details, "details");
        ValidationExceptionBuilder validationBuilder = new ValidationExceptionBuilder(this.i18nResolver);
        Iterable<ReadOnlyApplicationLink> existingLinks = this.getExistingLinks(applink.getId());
        this.validateSystemLink(applink, validationBuilder);
        this.validateUri("rpcUrl", details.getRpcUrl(), validationBuilder);
        this.validateUri("displayUrl", details.getDisplayUrl(), validationBuilder);
        this.validateDuplicates(details, existingLinks, validationBuilder);
        if (validationBuilder.hasErrors()) {
            throw validationBuilder.origin(details).build();
        }
    }

    private void validateNotBlank(String fieldName, String fieldValue, ValidationExceptionBuilder validationBuilder) {
        if (StringUtils.isBlank((CharSequence)fieldValue)) {
            validationBuilder.error(fieldName, "applinks.rest.invalidrepresentation", new Serializable[]{fieldName});
        }
    }

    private void validateSystemLink(ReadOnlyApplicationLink applink, ValidationExceptionBuilder validationBuilder) {
        if (applink.isSystem()) {
            validationBuilder.error("applinks.service.error.validation.applink.system", new Serializable[0]);
        }
    }

    private void validateUri(String context, URI uri, ValidationExceptionBuilder validationBuilder) {
        this.validateNotBlank(context, uri.toString(), validationBuilder);
        try {
            uri.toURL();
        }
        catch (MalformedURLException e) {
            validationBuilder.error(context, "applinks.service.error.validation.applink.url.malformed", new Serializable[0]);
        }
        catch (IllegalArgumentException e) {
            validationBuilder.error(context, "applinks.service.error.validation.applink.url.nonabsolute", new Serializable[0]);
        }
    }

    private void validateDuplicates(ApplicationLinkDetails details, Iterable<ReadOnlyApplicationLink> existingLinks, ValidationExceptionBuilder validationBuilder) {
        if (ApplinksStreams.toStream(existingLinks).anyMatch(ApplicationLinks.withName(details.getName()))) {
            validationBuilder.error("name", "applinks.service.error.validation.applink.duplicate.name", new Serializable[]{details.getName()});
        }
        if (ApplinksStreams.toStream(existingLinks).anyMatch(ApplicationLinks.withRpcUrl(details.getRpcUrl()))) {
            validationBuilder.error("rpcUrl", "applinks.service.error.validation.applink.duplicate.rpcUrl", new Serializable[0]);
        }
        if (ApplinksStreams.toStream(existingLinks).anyMatch(ApplicationLinks.withDisplayUrl(details.getDisplayUrl()))) {
            validationBuilder.error("displayUrl", "applinks.service.error.validation.applink.duplicate.displayUrl", new Serializable[0]);
        }
    }

    private Iterable<ReadOnlyApplicationLink> getExistingLinks(ApplicationId applicationId) {
        return ApplinksStreams.toStream(this.applicationLinkService.getApplicationLinks()).filter(ApplicationLinks.withId(applicationId).negate()).collect(ApplinksStreams.toImmutableList());
    }
}

