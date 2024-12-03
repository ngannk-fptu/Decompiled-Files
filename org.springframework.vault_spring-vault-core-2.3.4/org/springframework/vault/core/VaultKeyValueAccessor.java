/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

abstract class VaultKeyValueAccessor
implements VaultKeyValueOperationsSupport {
    private final VaultOperations vaultOperations;
    private final String path;
    private final ObjectMapper mapper;

    VaultKeyValueAccessor(VaultOperations vaultOperations, String path) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations = vaultOperations;
        this.path = path;
        this.mapper = VaultKeyValueAccessor.extractObjectMapper(vaultOperations);
    }

    @Override
    public void delete(String path) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations.doWithSession(restOperations -> {
            restOperations.exchange(this.createDataPath(path), HttpMethod.DELETE, null, Void.class, new Object[0]);
            return null;
        });
    }

    @Nullable
    <I, T> T doRead(String path, Class<I> deserializeAs, BiFunction<VaultResponseSupport<?>, I, T> mappingFunction) {
        ParameterizedTypeReference<VaultResponseSupport<JsonNode>> ref = VaultResponses.getTypeReference(JsonNode.class);
        VaultResponseSupport<JsonNode> response = this.doRead(this.createDataPath(path), ref);
        if (response != null) {
            JsonNode jsonNode = this.getJsonNode(response);
            JsonNode jsonMeta = response.getRequiredData().at("/metadata");
            response.setMetadata((Map)this.mapper.convertValue((Object)jsonMeta, (TypeReference)new TypeReference<Map<String, Object>>(){}));
            return mappingFunction.apply(response, this.deserialize(jsonNode, deserializeAs));
        }
        return null;
    }

    @Nullable
    <T> T doRead(String path, ParameterizedTypeReference<T> typeReference) {
        return this.doRead(restOperations -> restOperations.exchange(path, HttpMethod.GET, null, typeReference, new Object[0]));
    }

    <T> T deserialize(JsonNode jsonNode, Class<T> type) {
        try {
            return (T)this.mapper.reader().readValue(jsonNode.traverse(), type);
        }
        catch (IOException e) {
            throw new VaultException("Cannot deserialize response", e);
        }
    }

    @Nullable
    <T> T doRead(Function<RestOperations, ResponseEntity<T>> callback) {
        return (T)this.vaultOperations.doWithSession(restOperations -> {
            try {
                return ((ResponseEntity)callback.apply(restOperations)).getBody();
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw VaultResponses.buildException(e, this.path);
            }
        });
    }

    @Nullable
    VaultResponse doWrite(String path, Object body) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        try {
            return this.vaultOperations.doWithSession(restOperations -> (VaultResponse)restOperations.exchange(path, HttpMethod.POST, new HttpEntity(body), VaultResponse.class, new Object[0]).getBody());
        }
        catch (HttpStatusCodeException e) {
            throw VaultResponses.buildException(e, path);
        }
    }

    abstract JsonNode getJsonNode(VaultResponseSupport<JsonNode> var1);

    abstract String createDataPath(String var1);

    private static ObjectMapper extractObjectMapper(VaultOperations vaultOperations) {
        Optional mapper = vaultOperations.doWithSession(operations -> {
            if (operations instanceof RestTemplate) {
                RestTemplate template = (RestTemplate)operations;
                Optional<AbstractJackson2HttpMessageConverter> jackson2Converter = template.getMessageConverters().stream().filter(AbstractJackson2HttpMessageConverter.class::isInstance).map(AbstractJackson2HttpMessageConverter.class::cast).findFirst();
                return jackson2Converter.map(AbstractJackson2HttpMessageConverter::getObjectMapper);
            }
            return Optional.empty();
        });
        return mapper.orElseGet(ObjectMapper::new);
    }
}

