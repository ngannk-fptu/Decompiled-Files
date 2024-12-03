/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 */
package com.atlassian.oauth2.client.rest.resource.validator;

import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.rest.api.RestClientConfiguration;
import com.atlassian.oauth2.client.rest.resource.validator.ValidationException;

public interface ClientConfigurationValidator {
    public ClientConfigurationEntity validateCreate(RestClientConfiguration var1) throws ValidationException;

    public ClientConfigurationEntity validateUpdate(RestClientConfiguration var1, ClientConfigurationEntity var2) throws ValidationException;
}

