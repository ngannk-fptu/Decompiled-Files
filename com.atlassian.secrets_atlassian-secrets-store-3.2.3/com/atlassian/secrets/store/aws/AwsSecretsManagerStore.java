/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.api.SecretStore
 *  com.atlassian.secrets.api.SecretStoreException
 *  com.fasterxml.jackson.core.JacksonException
 *  com.fasterxml.jackson.core.JsonPointer
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
 *  software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
 */
package com.atlassian.secrets.store.aws;

import com.atlassian.secrets.api.SecretStore;
import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.store.aws.AwsSecretsManagerParams;
import com.atlassian.secrets.store.aws.DefaultSecretsManagerClientFactory;
import com.atlassian.secrets.store.aws.SecretsManagerClientFactory;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class AwsSecretsManagerStore
implements SecretStore {
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsManagerStore.class);
    private final SecretsManagerClientFactory clientFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AwsSecretsManagerStore(SecretsManagerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public AwsSecretsManagerStore() {
        this(new DefaultSecretsManagerClientFactory());
    }

    public String get(String jsonParams) {
        try {
            AwsSecretsManagerParams secretsManagerParams = (AwsSecretsManagerParams)this.objectMapper.readValue(jsonParams, AwsSecretsManagerParams.class);
            SecretsManagerClient client = secretsManagerParams.getEndpointOverride() != null ? this.clientFactory.getClient(secretsManagerParams.getRegion(), URI.create(secretsManagerParams.getEndpointOverride())) : this.clientFactory.getClient(secretsManagerParams.getRegion());
            GetSecretValueRequest request = (GetSecretValueRequest)GetSecretValueRequest.builder().secretId(secretsManagerParams.getSecretId()).build();
            String rawSecretValue = client.getSecretValue(request).secretString();
            log.debug("Retrieved AWS secret: {}", (Object)secretsManagerParams);
            return secretsManagerParams.getSecretPointer() == null ? rawSecretValue : this.parseAwsSecretValue(rawSecretValue, secretsManagerParams.getSecretPointer());
        }
        catch (JacksonException e) {
            log.error("Problem when reading secret store configuration. Please review the JSON configuration string.");
            throw new SecretStoreException("Problem when reading secret store configuration. Please review the JSON configuration string.");
        }
        catch (Exception e) {
            log.error("Problem when getting the secret value: {}", (Object)e.getMessage());
            throw new SecretStoreException("Problem when getting the secret value", (Throwable)e);
        }
    }

    public String store(String plainTextData) {
        throw new UnsupportedOperationException("Encryption is currently not supported for AWS Secrets Manager");
    }

    private String parseAwsSecretValue(String rawSecretValue, String secretPointerParam) {
        boolean startsWithSlash = secretPointerParam.startsWith("/");
        if (!startsWithSlash) {
            log.warn("Secret pointer '{}' does not start with a slash. We will add it, but please fix it in your configuration.", (Object)secretPointerParam);
        }
        String jsonPointer = startsWithSlash ? secretPointerParam : '/' + secretPointerParam;
        JsonPointer pointer = JsonPointer.compile((String)jsonPointer);
        JsonNode rootNode = this.parseJSONSecretValue(rawSecretValue);
        return rootNode.at(pointer).asText();
    }

    private JsonNode parseJSONSecretValue(String rawSecretValue) {
        try {
            return this.objectMapper.readTree(rawSecretValue);
        }
        catch (JsonProcessingException e) {
            throw new SecretStoreException("Could not parse AWS Secrets Manager value, value is not valid JSON.");
        }
    }
}

