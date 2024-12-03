/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.vault.core.VaultKeyValueAccessor;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultListResponse;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

abstract class VaultKeyValue2Accessor
extends VaultKeyValueAccessor {
    final String path;

    VaultKeyValue2Accessor(VaultOperations vaultOperations, String path) {
        super(vaultOperations, path);
        this.path = path;
    }

    @Override
    @Nullable
    public List<String> list(String path) {
        String pathToUse = path.equals("/") ? "" : (path.endsWith("/") ? path : path + "/");
        VaultListResponse read = (VaultListResponse)this.doRead(restOperations -> restOperations.exchange(String.format("%s?list=true", this.createBackendPath("metadata", pathToUse)), HttpMethod.GET, null, VaultListResponse.class, new Object[0]));
        if (read == null) {
            return Collections.emptyList();
        }
        return (List)((Map)read.getRequiredData()).get("keys");
    }

    @Override
    public VaultKeyValueOperationsSupport.KeyValueBackend getApiVersion() {
        return VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;
    }

    @Override
    JsonNode getJsonNode(VaultResponseSupport<JsonNode> response) {
        return response.getRequiredData().at("/data");
    }

    @Override
    String createDataPath(String path) {
        return this.createBackendPath("data", path);
    }

    String createBackendPath(String segment, String path) {
        return String.format("%s/%s/%s", this.path, segment, path);
    }
}

