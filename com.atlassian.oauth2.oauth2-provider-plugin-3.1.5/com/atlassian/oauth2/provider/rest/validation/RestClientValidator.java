/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.validation;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.atlassian.oauth2.common.rest.validator.RestValidator;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.rest.exception.ValidationException;
import com.atlassian.oauth2.provider.rest.model.RestClientEntity;
import com.atlassian.oauth2.provider.rest.validation.RestValidatorField;
import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientValidator
extends RestValidator {
    private static final Logger log = LoggerFactory.getLogger(RestClientValidator.class);
    private static final int MAX_STRING_LENGTH_FOR_REDIRECTS = 450;
    @VisibleForTesting
    static final String DUPLICATE_NAME = "oauth2.rest.error.settings.name.field.unique.warning.message";
    @VisibleForTesting
    static final String REDIRECT_TOO_LONG = "oauth2.rest.error.settings.redirect.field.too.long";
    @VisibleForTesting
    static final String FRAGMENT_PRESENT = "oauth2.rest.error.settings.redirect.field.fragment.present";
    @VisibleForTesting
    static final String URI_WAS_NOT_ABSOLUTE = "oauth2.rest.error.settings.redirect.field.not.absolute";
    @VisibleForTesting
    static final String INVALID_SCHEME = "oauth2.rest.error.settings.redirect.field.invalid.scheme";
    @VisibleForTesting
    static final String INVALID_URI = "oauth2.rest.error.settings.redirect.field.invalid.uri";
    @VisibleForTesting
    static final String INVALID_REDIRECT = "oauth2.rest.error.settings.redirect.field.invalid";
    private static final String USERKEY_DOES_NOT_EXIST = "oauth2.rest.error.userkey.does.not.exist";
    @VisibleForTesting
    static final String INVALID_SCOPE = "oauth2.request.scope.not.valid.error.message";
    private final I18nResolver i18nResolver;
    private final UserManager userManager;
    private final ClientService clientService;
    private final ScopeResolver scopeResolver;

    public RestClientValidator(I18nResolver i18nResolver, UserManager userManager, ClientService clientService, ScopeResolver scopeResolver) {
        super(i18nResolver);
        this.i18nResolver = i18nResolver;
        this.userManager = userManager;
        this.clientService = clientService;
        this.scopeResolver = scopeResolver;
    }

    public RestClientEntity validateCreate(@Nonnull RestClientEntity client) throws ValidationException {
        return this.validate(client);
    }

    public RestClientEntity validateUpdate(@Nonnull Client oldClient, @Nonnull RestClientEntity updatedClient) throws ValidationException {
        return this.validate(this.merge(oldClient, updatedClient));
    }

    private RestClientEntity merge(@Nonnull Client oldClient, @Nonnull RestClientEntity updatedClient) {
        return RestClientEntity.builder().id(updatedClient.getId()).name(StringUtils.isBlank((CharSequence)updatedClient.getName()) ? oldClient.getName() : updatedClient.getName()).redirects(updatedClient.getRedirects().isEmpty() ? oldClient.getRedirects() : updatedClient.getRedirects()).clientId(oldClient.getClientId()).clientSecret(oldClient.getClientSecret()).userKey(oldClient.getUserKey()).scope(StringUtils.isBlank((CharSequence)updatedClient.getScope()) ? oldClient.getScope().toString() : updatedClient.getScope()).build();
    }

    private RestClientEntity validate(RestClientEntity client) throws ValidationException {
        String userkey = this.getUserKeyOrThrowAuthorisationException();
        ErrorCollection.Builder errors = ErrorCollection.builder();
        RestClientEntity.RestClientEntityBuilder restClientBuilder = RestClientEntity.builder().id(client.getId()).clientId(client.getClientId()).name(this.checkNameUnique(errors, client.getClientId(), this.checkNotTooLong(errors, RestValidatorField.NAME.toString(), this.checkNotEmpty(errors, RestValidatorField.NAME.toString(), client.getName())))).redirects(this.checkRedirects(errors, client.getRedirects())).scope(this.checkScope(errors, client.getScope())).userKey(userkey);
        this.throwOnError(errors);
        return restClientBuilder.build();
    }

    private String getUserKeyOrThrowAuthorisationException() {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        if (remoteUserKey != null) {
            return remoteUserKey.getStringValue();
        }
        throw new AuthorisationException(this.i18nResolver.getText(USERKEY_DOES_NOT_EXIST));
    }

    private String checkNameUnique(ErrorCollection.Builder errors, String id, String clientName) {
        this.checkField(errors, RestValidatorField.NAME.toString(), this.clientService.isClientNameUnique(id, clientName), () -> this.i18nResolver.getText(DUPLICATE_NAME));
        return clientName;
    }

    private List<String> checkRedirects(ErrorCollection.Builder errors, List<String> redirects) {
        List<String> filteredRedirects = Optional.ofNullable(redirects).map(Collection::stream).orElseGet(Stream::empty).filter(StringUtils::isNotBlank).sorted().distinct().collect(Collectors.toList());
        this.checkUrisAreValid(errors, filteredRedirects);
        return this.checkNotEmpty(errors, RestValidatorField.REDIRECTS.toString(), filteredRedirects);
    }

    private void checkUrisAreValid(ErrorCollection.Builder errors, List<String> fieldValue) {
        this.checkField(errors, RestValidatorField.REDIRECTS.toString(), this.hasValidRedirects(fieldValue), (String result) -> this.i18nResolver.getText(INVALID_REDIRECT, new Serializable[]{result}));
    }

    private String hasValidRedirects(List<String> uris) {
        return uris.stream().map(this::isUriValid).flatMap(Collection::stream).distinct().collect(Collectors.joining(", "));
    }

    private List<String> isUriValid(String uri) {
        ArrayList<String> errors = new ArrayList<String>();
        if (uri.length() > 450) {
            errors.add(this.i18nResolver.getText(REDIRECT_TOO_LONG));
        }
        try {
            this.checkUriParts(URI.create(uri), errors);
        }
        catch (IllegalArgumentException e) {
            errors.add(this.i18nResolver.getText(INVALID_URI));
        }
        return errors;
    }

    private void checkUriParts(URI uri, List<String> errors) {
        if (uri.getFragment() != null) {
            errors.add(this.i18nResolver.getText(FRAGMENT_PRESENT));
        }
        if (!uri.isAbsolute()) {
            errors.add(this.i18nResolver.getText(URI_WAS_NOT_ABSOLUTE));
        }
        if (!this.isValidScheme(uri)) {
            errors.add(this.i18nResolver.getText(INVALID_SCHEME));
        }
    }

    private boolean isValidScheme(URI uri) {
        return SystemProperty.SKIP_REDIRECT_URL_HTTPS_REQUIREMENT.getValue() != false || !"http".equalsIgnoreCase(uri.getScheme()) || SystemProperty.DISALLOW_LOCALHOST_REDIRECT.getValue() == false && "localhost".equalsIgnoreCase(uri.getHost()) || SystemProperty.DEV_MODE.getValue() != false;
    }

    private String checkScope(ErrorCollection.Builder errors, String scope) {
        try {
            return this.scopeResolver.getScope(scope).getName();
        }
        catch (InvalidScopeException e) {
            log.debug("Failed to get scope", (Throwable)e);
            this.checkField(errors, RestValidatorField.SCOPE.toString(), false, () -> this.i18nResolver.getText(INVALID_SCOPE, new Serializable[]{scope}));
            return scope;
        }
    }

    private void throwOnError(@Nonnull ErrorCollection.Builder errors) throws ValidationException {
        if (errors.hasAnyErrors()) {
            throw new ValidationException(errors.build());
        }
    }
}

