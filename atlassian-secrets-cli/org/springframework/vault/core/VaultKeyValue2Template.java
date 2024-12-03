/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.SecretNotFoundException;
import org.springframework.vault.core.VaultKeyValue2Accessor;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

class VaultKeyValue2Template
extends VaultKeyValue2Accessor
implements VaultKeyValueOperations {
    public VaultKeyValue2Template(VaultOperations vaultOperations, String path) {
        super(vaultOperations, path);
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
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull(patch, "Patch body must not be null");
        VaultResponse readResponse = this.get(path);
        if (readResponse == null || readResponse.getData() == null) {
            throw new SecretNotFoundException(String.format("No data found at %s; patch only works on existing data", this.createDataPath(path)), String.format("%s/%s", this.path, path));
        }
        if (readResponse.getMetadata() == null) {
            throw new VaultException("Metadata must not be null");
        }
        Map<String, Object> metadata = readResponse.getMetadata();
        LinkedHashMap data = new LinkedHashMap((Map)readResponse.getRequiredData());
        data.putAll(patch);
        HashMap<String, Map<String, Object>> body = new HashMap<String, Map<String, Object>>();
        body.put("data", data);
        body.put("options", Collections.singletonMap("cas", metadata.get("version")));
        try {
            this.doWrite(this.createDataPath(path), body);
            return true;
        }
        catch (VaultException e) {
            if (e.getMessage() != null && (e.getMessage().contains("check-and-set") || e.getMessage().contains("did not match the current version"))) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public void put(String path, Object body) {
        Assert.hasText(path, "Path must not be empty");
        this.doWrite(this.createDataPath(path), Collections.singletonMap("data", body));
    }
}

