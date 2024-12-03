/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.rest.validation.grant;

import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.rest.exception.InvalidClientException;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public abstract class GrantValidator {
    protected I18nResolver i18nResolver;
    protected ClientService clientService;

    protected GrantValidator(I18nResolver i18nResolver, ClientService clientService) {
        this.i18nResolver = i18nResolver;
        this.clientService = clientService;
    }

    public void validate(TokenRequestFormParams formParams) {
        this.validateGrantSpecificConstraints(formParams);
        this.authenticateClient(formParams);
    }

    protected abstract void validateGrantSpecificConstraints(TokenRequestFormParams var1);

    private void authenticateClient(TokenRequestFormParams params) throws InvalidClientException {
        if (!this.clientService.isClientSecretValid(params.getClientId(), params.getClientSecret())) {
            throw new InvalidClientException(this.i18nResolver.getText("oauth2.rest.error.unauthenticated.client"));
        }
    }

    protected void validateRequiredParams(Map<String, String> requiredParams) throws InvalidRequestException {
        Optional<Map.Entry> missingParam = requiredParams.entrySet().stream().filter(entry -> StringUtils.isBlank((CharSequence)((CharSequence)entry.getValue()))).findFirst();
        if (missingParam.isPresent()) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.missing.required.parameter", new Serializable[]{(Serializable)missingParam.get().getKey()}));
        }
    }
}

