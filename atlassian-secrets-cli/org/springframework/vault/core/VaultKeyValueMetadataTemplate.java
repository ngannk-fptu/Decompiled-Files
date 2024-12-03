/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.core.VaultKeyValueMetadataOperations;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.DurationParser;
import org.springframework.vault.support.VaultMetadataRequest;
import org.springframework.vault.support.VaultMetadataResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.Versioned;

class VaultKeyValueMetadataTemplate
implements VaultKeyValueMetadataOperations {
    private final VaultOperations vaultOperations;
    private final String basePath;

    VaultKeyValueMetadataTemplate(VaultOperations vaultOperations, String basePath) {
        Assert.notNull((Object)vaultOperations, "VaultOperations must not be null");
        this.vaultOperations = vaultOperations;
        this.basePath = basePath;
    }

    @Override
    public VaultMetadataResponse get(String path) {
        VaultResponseSupport<Map> response = this.vaultOperations.read(this.getPath(path), Map.class);
        return response != null ? VaultKeyValueMetadataTemplate.fromMap(response.getRequiredData()) : null;
    }

    @Override
    public void put(String path, VaultMetadataRequest body) {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull((Object)body, "Body must not be null");
        this.vaultOperations.write(this.getPath(path), body);
    }

    @Override
    public void delete(String path) {
        Assert.hasText(path, "Path must not be empty");
        this.vaultOperations.delete(this.getPath(path));
    }

    private String getPath(String path) {
        Assert.hasText(path, "Path must not be empty");
        return this.basePath + "/metadata/" + path;
    }

    private static VaultMetadataResponse fromMap(Map<String, Object> metadataResponse) {
        Duration duration = DurationParser.parseDuration((String)metadataResponse.get("delete_version_after"));
        return VaultMetadataResponse.builder().casRequired(Boolean.parseBoolean(String.valueOf(metadataResponse.get("cas_required")))).createdTime(VaultKeyValueMetadataTemplate.toInstant((String)metadataResponse.get("created_time"))).currentVersion(Integer.parseInt(String.valueOf(metadataResponse.get("current_version")))).deleteVersionAfter(duration).maxVersions(Integer.parseInt(String.valueOf(metadataResponse.get("max_versions")))).oldestVersion(Integer.parseInt(String.valueOf(metadataResponse.get("oldest_version")))).updatedTime(VaultKeyValueMetadataTemplate.toInstant((String)metadataResponse.get("updated_time"))).versions(VaultKeyValueMetadataTemplate.buildVersions((Map)metadataResponse.get("versions"))).build();
    }

    private static List<Versioned.Metadata> buildVersions(Map<String, Map<String, Object>> versions) {
        return versions.entrySet().stream().map(entry -> VaultKeyValueMetadataTemplate.buildVersion((String)entry.getKey(), (Map)entry.getValue())).collect(Collectors.toList());
    }

    private static Versioned.Metadata buildVersion(String version, Map<String, Object> versionData) {
        Instant createdTime = VaultKeyValueMetadataTemplate.toInstant((String)versionData.get("created_time"));
        Instant deletionTime = VaultKeyValueMetadataTemplate.toInstant((String)versionData.get("deletion_time"));
        boolean destroyed = (Boolean)versionData.get("destroyed");
        Versioned.Version kvVersion = Versioned.Version.from(Integer.parseInt(version));
        return Versioned.Metadata.builder().createdAt(createdTime).deletedAt(deletionTime).destroyed(destroyed).version(kvVersion).build();
    }

    @Nullable
    private static Instant toInstant(String date) {
        return StringUtils.hasText(date) ? Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(date)) : null;
    }
}

