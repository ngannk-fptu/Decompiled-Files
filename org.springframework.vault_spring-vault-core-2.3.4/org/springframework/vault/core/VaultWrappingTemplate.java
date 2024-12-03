/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.core;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultHttpHeaders;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultWrappingOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.WrappedMetadata;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

public class VaultWrappingTemplate
implements VaultWrappingOperations {
    private final VaultOperations vaultOperations;

    public VaultWrappingTemplate(VaultOperations vaultOperations) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        this.vaultOperations = vaultOperations;
    }

    @Override
    @Nullable
    public WrappedMetadata lookup(VaultToken token) {
        Assert.notNull((Object)token, (String)"token VaultToken not be null");
        VaultResponse response = null;
        try {
            response = this.vaultOperations.write("sys/wrapping/lookup", Collections.singletonMap("token", token.getToken()));
        }
        catch (VaultException e) {
            if (e.getMessage() != null && e.getMessage().contains("does not exist")) {
                return null;
            }
            throw e;
        }
        if (response == null) {
            return null;
        }
        return VaultWrappingTemplate.getWrappedMetadata((Map)response.getData(), token);
    }

    @Override
    @Nullable
    public VaultResponse read(VaultToken token) {
        return this.doUnwrap(token, (restOperations, entity) -> (VaultResponse)restOperations.exchange("sys/wrapping/unwrap", HttpMethod.POST, entity, VaultResponse.class, new Object[0]).getBody());
    }

    @Override
    @Nullable
    public <T> VaultResponseSupport<T> read(VaultToken token, Class<T> responseType) {
        ParameterizedTypeReference ref = VaultResponses.getTypeReference(responseType);
        return this.doUnwrap(token, (restOperations, entity) -> (VaultResponseSupport)restOperations.exchange("sys/wrapping/unwrap", HttpMethod.POST, entity, ref, new Object[0]).getBody());
    }

    @Nullable
    private <T extends VaultResponseSupport<?>> T doUnwrap(VaultToken token, BiFunction<RestOperations, HttpEntity<?>, T> requestFunction) {
        return (T)this.vaultOperations.doWithVault(restOperations -> {
            try {
                return (VaultResponseSupport)requestFunction.apply(restOperations, new HttpEntity((MultiValueMap)VaultHttpHeaders.from(token)));
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST && e.getResponseBodyAsString().contains("does not exist")) {
                    return null;
                }
                throw VaultResponses.buildException(e, "sys/wrapping/unwrap");
            }
        });
    }

    @Override
    public WrappedMetadata rewrap(VaultToken token) {
        Assert.notNull((Object)token, (String)"token VaultToken not be null");
        VaultResponse response = this.vaultOperations.write("sys/wrapping/rewrap", Collections.singletonMap("token", token.getToken()));
        Map<String, String> wrapInfo = response.getWrapInfo();
        return VaultWrappingTemplate.getWrappedMetadata(wrapInfo, VaultToken.of(wrapInfo.get("token")));
    }

    @Override
    public WrappedMetadata wrap(Object body, Duration duration) {
        Assert.notNull((Object)body, (String)"Body must not be null");
        Assert.notNull((Object)duration, (String)"TTL duration must not be null");
        VaultResponse response = this.vaultOperations.doWithSession(restOperations -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Vault-Wrap-TTL", Long.toString(duration.getSeconds()));
            return (VaultResponse)restOperations.exchange("sys/wrapping/wrap", HttpMethod.POST, new HttpEntity(body, (MultiValueMap)headers), VaultResponse.class, new Object[0]).getBody();
        });
        Map<String, String> wrapInfo = response.getWrapInfo();
        return VaultWrappingTemplate.getWrappedMetadata(wrapInfo, VaultToken.of(wrapInfo.get("token")));
    }

    private static WrappedMetadata getWrappedMetadata(Map<String, ?> wrapInfo, VaultToken token) {
        TemporalAccessor creation_time = VaultWrappingTemplate.getDate(wrapInfo, "creation_time");
        String path = (String)wrapInfo.get("creation_path");
        Duration ttl = VaultWrappingTemplate.getTtl(wrapInfo);
        return new WrappedMetadata(token, ttl, Instant.from(creation_time), path);
    }

    @Nullable
    private static TemporalAccessor getDate(Map<String, ?> responseMetadata, String key) {
        String date = responseMetadata.getOrDefault(key, "");
        return StringUtils.hasText((String)date) ? DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(date) : null;
    }

    @Nullable
    private static Duration getTtl(Map<String, ?> wrapInfo) {
        Object creationTtl = wrapInfo.get("ttl");
        if (creationTtl == null) {
            creationTtl = wrapInfo.get("creation_ttl");
        }
        if (creationTtl instanceof String) {
            creationTtl = Integer.parseInt((String)creationTtl);
        }
        Duration ttl = null;
        if (creationTtl instanceof Integer) {
            ttl = Duration.ofSeconds(((Integer)creationTtl).intValue());
        }
        return ttl;
    }
}

