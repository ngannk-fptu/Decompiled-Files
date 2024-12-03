/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultKeyValue2Accessor;
import org.springframework.vault.core.VaultKeyValueMetadataOperations;
import org.springframework.vault.core.VaultKeyValueMetadataTemplate;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.Versioned;
import org.springframework.web.client.HttpStatusCodeException;

public class VaultVersionedKeyValueTemplate
extends VaultKeyValue2Accessor
implements VaultVersionedKeyValueOperations {
    private final VaultOperations vaultOperations;
    private final String path;

    public VaultVersionedKeyValueTemplate(VaultOperations vaultOperations, String path) {
        super(vaultOperations, path);
        this.vaultOperations = vaultOperations;
        this.path = path;
    }

    @Nullable
    public Versioned<Map<String, Object>> get(String path, Versioned.Version version) {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull((Object)version, "Version must not be null");
        return this.doRead(path, version, Map.class);
    }

    @Override
    @Nullable
    public <T> Versioned<T> get(String path, Versioned.Version version, Class<T> responseType) {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull((Object)version, "Version must not be null");
        Assert.notNull(responseType, "Response type must not be null");
        return this.doRead(path, version, responseType);
    }

    @Nullable
    private <T> Versioned<T> doRead(String path, Versioned.Version version, Class<T> responseType) {
        String secretPath = version.isVersioned() ? String.format("%s?version=%d", this.createDataPath(path), version.getVersion()) : this.createDataPath(path);
        VersionedResponse response = this.vaultOperations.doWithSession(restOperations -> {
            try {
                return (VersionedResponse)restOperations.exchange(secretPath, HttpMethod.GET, null, VersionedResponse.class, new Object[0]).getBody();
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    if (e.getResponseBodyAsString().contains("deletion_time")) {
                        return VaultResponses.unwrap(e.getResponseBodyAsString(), VersionedResponse.class);
                    }
                    return null;
                }
                throw VaultResponses.buildException(e, path);
            }
        });
        if (response == null) {
            return null;
        }
        VaultResponseSupport data = (VaultResponseSupport)response.getRequiredData();
        Versioned.Metadata metadata = VaultVersionedKeyValueTemplate.getMetadata(data.getMetadata());
        T body = this.deserialize((JsonNode)data.getRequiredData(), responseType);
        return Versioned.create(body, metadata);
    }

    @Override
    public Versioned.Metadata put(String path, Object body) {
        Assert.hasText(path, "Path must not be empty");
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Integer> requestOptions = new LinkedHashMap<String, Integer>();
        if (body instanceof Versioned) {
            Versioned versioned = (Versioned)body;
            data.put("data", versioned.getData());
            data.put("options", requestOptions);
            requestOptions.put("cas", versioned.getVersion().getVersion());
        } else {
            data.put("data", body);
        }
        VaultResponse response = this.doWrite(this.createDataPath(path), data);
        if (response == null) {
            throw new IllegalStateException("VaultVersionedKeyValueOperations cannot be used with a Key-Value version 1 mount");
        }
        return VaultVersionedKeyValueTemplate.getMetadata((Map)response.getRequiredData());
    }

    private static Versioned.Metadata getMetadata(Map<String, Object> responseMetadata) {
        Versioned.Metadata.MetadataBuilder builder = Versioned.Metadata.builder();
        TemporalAccessor created_time = VaultVersionedKeyValueTemplate.getDate(responseMetadata, "created_time");
        TemporalAccessor deletion_time = VaultVersionedKeyValueTemplate.getDate(responseMetadata, "deletion_time");
        builder.createdAt(Instant.from(created_time));
        if (deletion_time != null) {
            builder.deletedAt(Instant.from(deletion_time));
        }
        if (Boolean.TRUE.equals(responseMetadata.get("destroyed"))) {
            builder.destroyed();
        }
        Integer version = (Integer)responseMetadata.get("version");
        builder.version(Versioned.Version.from(version));
        return builder.build();
    }

    @Nullable
    private static TemporalAccessor getDate(Map<String, Object> responseMetadata, String key) {
        String date = (String)responseMetadata.getOrDefault(key, "");
        if (StringUtils.hasText(date)) {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(date);
        }
        return null;
    }

    @Override
    public void delete(String path, Versioned.Version ... versionsToDelete) {
        Assert.hasText(path, "Path must not be empty");
        Assert.noNullElements((Object[])versionsToDelete, "Versions must not be null");
        if (versionsToDelete.length == 0) {
            this.delete(path);
            return;
        }
        List<Integer> versions = VaultVersionedKeyValueTemplate.toVersionList(versionsToDelete);
        this.doWrite(this.createBackendPath("delete", path), Collections.singletonMap("versions", versions));
    }

    private static List<Integer> toVersionList(Versioned.Version[] versionsToDelete) {
        return Arrays.stream(versionsToDelete).filter(Versioned.Version::isVersioned).map(Versioned.Version::getVersion).collect(Collectors.toList());
    }

    @Override
    public void undelete(String path, Versioned.Version ... versionsToDelete) {
        Assert.hasText(path, "Path must not be empty");
        Assert.noNullElements((Object[])versionsToDelete, "Versions must not be null");
        List<Integer> versions = VaultVersionedKeyValueTemplate.toVersionList(versionsToDelete);
        this.doWrite(this.createBackendPath("undelete", path), Collections.singletonMap("versions", versions));
    }

    @Override
    public void destroy(String path, Versioned.Version ... versionsToDelete) {
        Assert.hasText(path, "Path must not be empty");
        Assert.noNullElements((Object[])versionsToDelete, "Versions must not be null");
        List<Integer> versions = VaultVersionedKeyValueTemplate.toVersionList(versionsToDelete);
        this.doWrite(this.createBackendPath("destroy", path), Collections.singletonMap("versions", versions));
    }

    @Override
    public VaultKeyValueMetadataOperations opsForKeyValueMetadata() {
        return new VaultKeyValueMetadataTemplate(this.vaultOperations, this.path);
    }

    private static class VersionedResponse
    extends VaultResponseSupport<VaultResponseSupport<JsonNode>> {
        private VersionedResponse() {
        }
    }
}

