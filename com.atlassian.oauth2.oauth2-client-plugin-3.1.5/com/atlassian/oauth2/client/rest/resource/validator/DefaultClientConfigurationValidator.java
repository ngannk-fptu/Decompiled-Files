/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity$Builder
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.client.rest.resource.validator;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.client.RedirectUriSuffixGenerator;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.rest.api.RestClientConfiguration;
import com.atlassian.oauth2.client.rest.resource.validator.ClientConfigurationValidator;
import com.atlassian.oauth2.client.rest.resource.validator.ValidationException;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.atlassian.oauth2.common.rest.validator.RestValidator;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class DefaultClientConfigurationValidator
extends RestValidator
implements ClientConfigurationValidator {
    @VisibleForTesting
    static final List<ProviderType> PROVIDER_TYPES = Arrays.asList(ProviderType.values());
    private final ClientConfigStorageService clientConfigStorageService;
    private final I18nResolver i18nResolver;
    private final ClientHttpsValidator clientHttpsValidator;
    private final RedirectUriSuffixGenerator redirectUriSuffixGenerator;

    public DefaultClientConfigurationValidator(ClientConfigStorageService clientConfigStorageService, I18nResolver i18nResolver, ClientHttpsValidator clientHttpsValidator, RedirectUriSuffixGenerator redirectUriSuffixGenerator) {
        super(i18nResolver);
        this.clientConfigStorageService = clientConfigStorageService;
        this.i18nResolver = i18nResolver;
        this.clientHttpsValidator = clientHttpsValidator;
        this.redirectUriSuffixGenerator = redirectUriSuffixGenerator;
    }

    @Override
    public ClientConfigurationEntity validateCreate(RestClientConfiguration clientConfiguration) throws ValidationException {
        return this.validateClientConfiguration(clientConfiguration);
    }

    @Override
    public ClientConfigurationEntity validateUpdate(RestClientConfiguration updatedConfig, ClientConfigurationEntity existingConfig) throws ValidationException {
        if (Objects.isNull(updatedConfig.getClientSecret())) {
            RestClientConfiguration configWithSecret = new RestClientConfiguration(updatedConfig);
            configWithSecret.setClientSecret(existingConfig.getClientSecret());
            return this.validateClientConfiguration(configWithSecret);
        }
        return this.validateClientConfiguration(updatedConfig);
    }

    private ClientConfigurationEntity validateClientConfiguration(RestClientConfiguration clientConfiguration) throws ValidationException {
        ErrorCollection.Builder errors = ErrorCollection.builder();
        ClientConfigurationEntity.Builder entityBuilder = ClientConfigurationEntity.builder().clientId(this.checkNotTooLong(errors, "clientId", this.checkNotEmpty(errors, "clientId", clientConfiguration.getClientId()))).name(this.checkName(errors, clientConfiguration.getId(), clientConfiguration.getName())).description(this.checkNotTooLong(errors, "description", clientConfiguration.getDescription())).providerType(this.checkProviderType(errors, clientConfiguration.getType())).authorizationEndpoint(this.checkEndpoint(errors, "authorizationEndpoint", clientConfiguration.getAuthorizationEndpoint())).tokenEndpoint(this.checkEndpoint(errors, "tokenEndpoint", clientConfiguration.getTokenEndpoint())).clientSecret(this.checkNotTooLong(errors, "clientSecret", this.checkNotEmpty(errors, "clientSecret", clientConfiguration.getClientSecret()))).scopes(this.checkScopes(errors, clientConfiguration.getScopes()));
        this.checkRedirectUriSuffix(errors, clientConfiguration);
        this.throwOnError(errors);
        return entityBuilder.build();
    }

    private ProviderType checkProviderType(ErrorCollection.Builder errors, String key) {
        Optional providerType = ProviderType.get((String)key);
        this.checkField(errors, "type", providerType.isPresent(), () -> this.i18nResolver.getText("oauth2.rest.error.settings.field.type.invalid", new Serializable[]{"type", (Serializable)((Object)PROVIDER_TYPES), key}));
        return providerType.orElse(null);
    }

    private String checkEndpoint(ErrorCollection.Builder errors, String fieldName, String endpointValue) {
        this.checkNotTooLong(errors, fieldName, endpointValue);
        if (errors.hasNoErrors()) {
            this.checkField(errors, fieldName, this.clientHttpsValidator.isSecure(endpointValue) && this.isParseableUrl(endpointValue), () -> this.i18nResolver.getText("oauth2.rest.error.settings.endpoint.invalid", new Serializable[]{fieldName, endpointValue}));
        }
        return endpointValue;
    }

    private boolean isParseableUrl(String url) {
        try {
            URI.create(url);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private List<String> checkScopes(ErrorCollection.Builder errors, List<String> scopes) {
        List filteredScopes = Optional.ofNullable(scopes).map(Collection::stream).orElseGet(Stream::empty).filter(StringUtils::isNotBlank).sorted().distinct().collect(Collectors.toList());
        return this.checkNotEmpty(errors, "scopes", filteredScopes);
    }

    private String checkName(ErrorCollection.Builder errors, String id, String name) {
        this.checkNotTooLong(errors, "name", this.checkNotEmpty(errors, "name", name));
        if (errors.hasNoErrors()) {
            this.checkField(errors, "name", this.clientConfigStorageService.isNameUnique(id, name), () -> this.i18nResolver.getText("oauth2.rest.error.settings.name.duplicated", new Serializable[]{name}));
        }
        return name;
    }

    private void checkRedirectUriSuffix(ErrorCollection.Builder errors, RestClientConfiguration clientConfigurationToSave) {
        this.checkNotEmpty(errors, "redirectUriSuffix", clientConfigurationToSave.getRedirectUriSuffix());
        if (errors.hasNoErrors()) {
            String regeneratedRedirectUriSuffix = this.redirectUriSuffixGenerator.generateRedirectUriSuffix(clientConfigurationToSave.getAuthorizationEndpoint());
            boolean isValidSuffix = regeneratedRedirectUriSuffix.equals(clientConfigurationToSave.getRedirectUriSuffix());
            this.checkField(errors, "redirectUriSuffix", isValidSuffix, () -> this.i18nResolver.getText("oauth2.rest.error.settings.redirect.suffix.invalid", new Serializable[]{"redirectUriSuffix", regeneratedRedirectUriSuffix, clientConfigurationToSave.getRedirectUriSuffix()}));
        }
    }

    private void throwOnError(@Nonnull ErrorCollection.Builder errors) throws ValidationException {
        if (errors.hasAnyErrors()) {
            throw new ValidationException(errors.build());
        }
    }

    @VisibleForTesting
    static interface Error {
        public static final String INVALID_TYPE = "oauth2.rest.error.settings.field.type.invalid";
        public static final String INVALID_ENDPOINT = "oauth2.rest.error.settings.endpoint.invalid";
        public static final String INVALID_SUFFIX = "oauth2.rest.error.settings.redirect.suffix.invalid";
        public static final String DUPLICATE_NAME = "oauth2.rest.error.settings.name.duplicated";
    }

    @VisibleForTesting
    static interface Field {
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String CLIENT_ID = "clientId";
        public static final String CLIENT_SECRET = "clientSecret";
        public static final String AUTHORIZATION_ENDPOINT = "authorizationEndpoint";
        public static final String TOKEN_ENDPOINT = "tokenEndpoint";
        public static final String SCOPES = "scopes";
        public static final String REDIRECT_URI_SUFFIX = "redirectUriSuffix";
    }
}

