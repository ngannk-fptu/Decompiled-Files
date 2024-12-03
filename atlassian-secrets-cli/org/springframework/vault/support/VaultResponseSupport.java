/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VaultResponseSupport<T> {
    @Nullable
    private Map<String, Object> auth;
    @Nullable
    private T data;
    @Nullable
    private Map<String, Object> metadata;
    @JsonProperty(value="wrap_info")
    @Nullable
    private Map<String, String> wrapInfo;
    @JsonProperty(value="lease_duration")
    private long leaseDuration;
    @JsonProperty(value="lease_id")
    @Nullable
    private String leaseId;
    @JsonProperty(value="request_id")
    @Nullable
    private String requestId;
    private boolean renewable;
    @Nullable
    private List<String> warnings;

    @Nullable
    public Map<String, Object> getAuth() {
        return this.auth;
    }

    public Map<String, Object> getRequiredAuth() {
        if (this.auth != null) {
            return this.auth;
        }
        throw new IllegalStateException("Auth field is empty");
    }

    public void setAuth(@Nullable Map<String, Object> auth) {
        this.auth = auth;
    }

    @Nullable
    public T getData() {
        return this.data;
    }

    public T getRequiredData() {
        if (this.data != null) {
            return this.data;
        }
        throw new IllegalStateException("Data field is empty");
    }

    public void setData(@Nullable T data) {
        this.data = data;
    }

    @Nullable
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(@Nullable Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public long getLeaseDuration() {
        return this.leaseDuration;
    }

    public void setLeaseDuration(long leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    @Nullable
    public String getLeaseId() {
        return this.leaseId;
    }

    public void setLeaseId(@Nullable String leaseId) {
        this.leaseId = leaseId;
    }

    public boolean isRenewable() {
        return this.renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    @Nullable
    public Map<String, String> getWrapInfo() {
        return this.wrapInfo;
    }

    public void setWrapInfo(@Nullable Map<String, String> wrapInfo) {
        this.wrapInfo = wrapInfo;
    }

    @Nullable
    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    @Nullable
    public List<String> getWarnings() {
        return this.warnings;
    }

    public void setWarnings(@Nullable List<String> warnings) {
        this.warnings = warnings;
    }
}

