/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultKeyValueAccessor;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

class VaultKeyValue1Template
extends VaultKeyValueAccessor
implements VaultKeyValueOperations {
    private final VaultOperations vaultOperations;
    private final String path;

    public VaultKeyValue1Template(VaultOperations vaultOperations, String path) {
        super(vaultOperations, path);
        this.vaultOperations = vaultOperations;
        this.path = path;
    }

    @Override
    @Nullable
    public List<String> list(String path) {
        return this.vaultOperations.list(this.createDataPath(path));
    }

    @Override
    @Nullable
    public VaultResponse get(String path) {
        Assert.hasText(path, "Path must not be empty");
        return this.doRead(path, Map.class, (response, data) -> {
            VaultResponse vaultResponse = new VaultResponse();
            vaultResponse.setRenewable(response.isRenewable());
            vaultResponse.setAuth(response.getAuth());
            vaultResponse.setLeaseDuration(response.getLeaseDuration());
            vaultResponse.setLeaseId(response.getLeaseId());
            vaultResponse.setMetadata(response.getMetadata());
            vaultResponse.setRequestId(response.getRequestId());
            vaultResponse.setWarnings(response.getWarnings());
            vaultResponse.setWrapInfo(response.getWrapInfo());
            vaultResponse.setData(data);
            return vaultResponse;
        });
    }

    @Override
    @Nullable
    public <T> VaultResponseSupport<T> get(String path, Class<T> responseType) {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull(responseType, "Response type must not be null");
        return this.doRead(path, responseType, (response, data) -> {
            VaultResponseSupport result = response;
            result.setData(data);
            return result;
        });
    }

    @Override
    public boolean patch(String path, Map<String, ?> patch) {
        throw new IllegalStateException("K/V engine mount must be version 2 for patch support");
    }

    @Override
    public void put(String path, Object body) {
        Assert.hasText(path, "Path must not be empty");
        this.doWrite(this.createDataPath(path), body);
    }

    @Override
    public VaultKeyValueOperationsSupport.KeyValueBackend getApiVersion() {
        return VaultKeyValueOperationsSupport.KeyValueBackend.KV_1;
    }

    @Override
    JsonNode getJsonNode(VaultResponseSupport<JsonNode> response) {
        return response.getRequiredData();
    }

    @Override
    String createDataPath(String path) {
        return String.format("%s/%s", this.path, path);
    }
}

