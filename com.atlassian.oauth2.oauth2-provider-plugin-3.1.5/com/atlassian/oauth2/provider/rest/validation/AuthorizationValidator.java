/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.authorization.SupportedResponseType
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.rest.validation;

import com.atlassian.oauth2.provider.api.authorization.SupportedResponseType;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.exception.UnsupportedResponseTypeException;
import com.atlassian.oauth2.provider.rest.model.RestAuthorizationRequest;
import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class AuthorizationValidator {
    private final I18nResolver i18nResolver;
    private final ScopeResolver scopeResolver;
    private final PkceService pkceService;
    private final ClientService clientService;

    public AuthorizationValidator(I18nResolver i18nResolver, ScopeResolver scopeResolver, PkceService pkceService, ClientService clientService) {
        this.i18nResolver = i18nResolver;
        this.scopeResolver = scopeResolver;
        this.pkceService = pkceService;
        this.clientService = clientService;
    }

    public void validateRedirectToConsentScreen(RestAuthorizationRequest request) throws InvalidRequestException {
        this.checkParameter(request.getClientId(), "client_id");
        this.checkParameter(request.getRedirectUri(), "redirect_uri");
        this.checkParameter(request.getResponseType(), "response_type");
        this.checkParameter(request.getScope(), "scope");
    }

    private void checkParameter(String parameter, String parameterName) throws InvalidRequestException {
        if (StringUtils.isEmpty((CharSequence)parameter)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.validation.error.message", new Serializable[]{parameterName}));
        }
    }

    public void validateForResourceOwner(Optional<Client> client, RestAuthorizationRequest request) throws InvalidRequestException {
        if (!client.isPresent()) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.validation.error.message", new Serializable[]{"client_id"}));
        }
        if (!this.isRedirectValid(client.get(), request.getRedirectUri())) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.validation.error.message", new Serializable[]{"redirect_uri"}));
        }
    }

    private boolean isRedirectValid(Client client, String redirect) {
        return StringUtils.isNotBlank((CharSequence)redirect) && client.getRedirects().stream().anyMatch(clientRedirect -> clientRedirect.equals(redirect));
    }

    public void validateForClient(RestAuthorizationRequest request) throws InvalidRequestException, UnsupportedResponseTypeException {
        if (StringUtils.isBlank((CharSequence)request.getResponseType())) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.response.type.is.empty.error.message"));
        }
        if (!SupportedResponseType.CODE.value.equals(request.getResponseType())) {
            throw new UnsupportedResponseTypeException(this.i18nResolver.getText("oauth2.authorization.unsupported.response.type.error.message"));
        }
    }

    public Optional<String> validateScope(String clientId, String scope) {
        return this.clientService.getByClientId(clientId).flatMap(client -> {
            if (this.isScopeValid((Client)client, scope)) {
                return Optional.empty();
            }
            return Optional.of(this.i18nResolver.getText("oauth2.request.scope.valid.for.client.validation.error.message", new Serializable[]{scope, client.getScope().toString()}));
        });
    }

    public void validateScope(Client client, RestAuthorizationRequest request) throws InvalidRequestException {
        if (!this.isScopeValid(client, request.getScope())) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.request.scope.valid.for.client.validation.error.message", new Serializable[]{client.getScope().toString(), request.getScope()}));
        }
    }

    private boolean isScopeValid(Client client, String scope) {
        try {
            return this.scopeResolver.hasScopePermission(this.scopeResolver.getScope(scope), client.getScope());
        }
        catch (InvalidScopeException e) {
            return false;
        }
    }

    public void validatePkceFields(String codeChallengeMethod, String codeChallenge) throws InvalidRequestException {
        if (StringUtils.isBlank((CharSequence)codeChallengeMethod)) {
            return;
        }
        if (CodeChallengeMethod.fromString((String)codeChallengeMethod) == null) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.pkce.code.challenge.method.invalid"));
        }
        if (codeChallenge == null) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.pkce.code.challenge.missing"));
        }
        if (!this.pkceService.isValidCode(codeChallenge)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.authorization.pkce.code.challenge.invalid"));
        }
    }
}

