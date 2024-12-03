/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.RestOperationsCallback;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.support.Policy;
import org.springframework.vault.support.VaultHealth;
import org.springframework.vault.support.VaultInitializationRequest;
import org.springframework.vault.support.VaultInitializationResponse;
import org.springframework.vault.support.VaultMount;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultUnsealStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

public class VaultSysTemplate
implements VaultSysOperations {
    private static final GetUnsealStatus GET_UNSEAL_STATUS = new GetUnsealStatus();
    private static final Seal SEAL = new Seal();
    private static final GetMounts GET_MOUNTS = new GetMounts("sys/mounts");
    private static final GetMounts GET_AUTH_MOUNTS = new GetMounts("sys/auth");
    private static final Health HEALTH = new Health();
    private static final ObjectMapper OBJECT_MAPPER;
    private final VaultOperations vaultOperations;

    public VaultSysTemplate(VaultOperations vaultOperations) {
        Assert.notNull((Object)vaultOperations, "VaultOperations must not be null");
        this.vaultOperations = vaultOperations;
    }

    @Override
    public boolean isInitialized() {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithSession(restOperations -> {
            try {
                ResponseEntity<Map> body = restOperations.exchange("sys/init", HttpMethod.GET, VaultSysTemplate.emptyNamespace(null), Map.class, new Object[0]);
                Assert.state(body.getBody() != null, "Initialization response must not be null");
                return (Boolean)((Map)body.getBody()).get("initialized");
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e);
            }
        }));
    }

    @Override
    public VaultInitializationResponse initialize(VaultInitializationRequest vaultInitializationRequest) {
        Assert.notNull((Object)vaultInitializationRequest, "VaultInitialization must not be null");
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithVault(restOperations -> {
            try {
                ResponseEntity<VaultInitializationResponseImpl> exchange2 = restOperations.exchange("sys/init", HttpMethod.PUT, VaultSysTemplate.emptyNamespace(vaultInitializationRequest), VaultInitializationResponseImpl.class, new Object[0]);
                Assert.state(exchange2.getBody() != null, "Initialization response must not be null");
                return (VaultInitializationResponseImpl)exchange2.getBody();
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e);
            }
        }));
    }

    @Override
    public void seal() {
        this.vaultOperations.doWithSession(SEAL);
    }

    @Override
    public VaultUnsealStatus unseal(String keyShare) {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithVault(restOperations -> {
            ResponseEntity<VaultUnsealStatusImpl> response = restOperations.exchange("sys/unseal", HttpMethod.PUT, new HttpEntity<Map<String, String>>(Collections.singletonMap("key", keyShare)), VaultUnsealStatusImpl.class, new Object[0]);
            Assert.state(response.getBody() != null, "Unseal response must not be null");
            return (VaultUnsealStatusImpl)response.getBody();
        }));
    }

    @Override
    public VaultUnsealStatus getUnsealStatus() {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithVault(GET_UNSEAL_STATUS));
    }

    @Override
    public void mount(String path, VaultMount vaultMount) {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull((Object)vaultMount, "VaultMount must not be null");
        this.vaultOperations.write(String.format("sys/mounts/%s", path), vaultMount);
    }

    @Override
    public Map<String, VaultMount> getMounts() {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithSession(GET_MOUNTS));
    }

    @Override
    public void unmount(String path) {
        Assert.hasText(path, "Path must not be empty");
        this.vaultOperations.delete(String.format("sys/mounts/%s", path));
    }

    @Override
    public void authMount(String path, VaultMount vaultMount) throws VaultException {
        Assert.hasText(path, "Path must not be empty");
        Assert.notNull((Object)vaultMount, "VaultMount must not be null");
        this.vaultOperations.write(String.format("sys/auth/%s", path), vaultMount);
    }

    @Override
    public Map<String, VaultMount> getAuthMounts() throws VaultException {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithSession(GET_AUTH_MOUNTS));
    }

    @Override
    public void authUnmount(String path) throws VaultException {
        Assert.hasText(path, "Path must not be empty");
        this.vaultOperations.delete(String.format("sys/auth/%s", path));
    }

    @Override
    public List<String> getPolicyNames() throws VaultException {
        return VaultSysTemplate.requireResponse((List)((Map)this.vaultOperations.read("sys/policy").getRequiredData()).get("policies"));
    }

    @Override
    @Nullable
    public Policy getPolicy(String name) throws VaultException {
        Assert.hasText(name, "Name must not be null or empty");
        return this.vaultOperations.doWithSession(restOperations -> {
            ResponseEntity<VaultResponse> response;
            try {
                response = restOperations.getForEntity("sys/policy/{name}", VaultResponse.class, name);
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw e;
            }
            String rules = (String)((Map)((VaultResponse)response.getBody()).getRequiredData()).get("rules");
            if (StringUtils.isEmpty(rules)) {
                return Policy.empty();
            }
            if (rules.trim().startsWith("{")) {
                return VaultResponses.unwrap(rules, Policy.class);
            }
            throw new UnsupportedOperationException("Cannot parse policy in HCL format");
        });
    }

    @Override
    public void createOrUpdatePolicy(String name, Policy policy) throws VaultException {
        String rules;
        Assert.hasText(name, "Name must not be null or empty");
        Assert.notNull((Object)policy, "Policy must not be null");
        try {
            rules = OBJECT_MAPPER.writeValueAsString(policy);
        }
        catch (IOException e) {
            throw new VaultException("Cannot serialize policy to JSON", e);
        }
        this.vaultOperations.doWithSession(restOperations -> {
            restOperations.exchange("sys/policy/{name}", HttpMethod.PUT, new HttpEntity<Map<String, String>>(Collections.singletonMap("rules", rules)), VaultResponse.class, name);
            return null;
        });
    }

    @Override
    public void deletePolicy(String name) throws VaultException {
        Assert.hasText(name, "Name must not be null or empty");
        this.vaultOperations.delete(String.format("sys/policy/%s", name));
    }

    @Override
    public VaultHealth health() {
        return VaultSysTemplate.requireResponse(this.vaultOperations.doWithVault(HEALTH));
    }

    private static <T> T requireResponse(@Nullable T response) {
        Assert.state(response != null, "Response must not be null");
        return response;
    }

    private static <T> HttpEntity<T> emptyNamespace(@Nullable T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Vault-Namespace", "");
        return new HttpEntity<T>(body, headers);
    }

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER = mapper;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class VaultHealthImpl
    implements VaultHealth {
        private final boolean initialized;
        private final boolean sealed;
        private final boolean standby;
        private final boolean performanceStandby;
        private final boolean replicationRecoverySecondary;
        private final int serverTimeUtc;
        @Nullable
        private final String version;

        VaultHealthImpl(@JsonProperty(value="initialized") boolean initialized, @JsonProperty(value="sealed") boolean sealed, @JsonProperty(value="standby") boolean standby, @JsonProperty(value="performance_standby") boolean performanceStandby, @Nullable @JsonProperty(value="replication_dr_mode") String replicationRecoverySecondary, @JsonProperty(value="server_time_utc") int serverTimeUtc, @Nullable @JsonProperty(value="version") String version) {
            this.initialized = initialized;
            this.sealed = sealed;
            this.standby = standby;
            this.performanceStandby = performanceStandby;
            this.replicationRecoverySecondary = replicationRecoverySecondary != null && !"disabled".equalsIgnoreCase(replicationRecoverySecondary);
            this.serverTimeUtc = serverTimeUtc;
            this.version = version;
        }

        @Override
        public boolean isInitialized() {
            return this.initialized;
        }

        @Override
        public boolean isSealed() {
            return this.sealed;
        }

        @Override
        public boolean isStandby() {
            return this.standby;
        }

        @Override
        public boolean isPerformanceStandby() {
            return this.performanceStandby;
        }

        @Override
        public boolean isRecoveryReplicationSecondary() {
            return this.replicationRecoverySecondary;
        }

        @Override
        public int getServerTimeUtc() {
            return this.serverTimeUtc;
        }

        @Override
        @Nullable
        public String getVersion() {
            return this.version;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VaultHealthImpl)) {
                return false;
            }
            VaultHealthImpl that = (VaultHealthImpl)o;
            return this.initialized == that.initialized && this.sealed == that.sealed && this.standby == that.standby && this.performanceStandby == that.performanceStandby && this.replicationRecoverySecondary == that.replicationRecoverySecondary && this.serverTimeUtc == that.serverTimeUtc && Objects.equals(this.version, that.version);
        }

        public int hashCode() {
            return Objects.hash(this.initialized, this.sealed, this.standby, this.performanceStandby, this.replicationRecoverySecondary, this.serverTimeUtc, this.version);
        }
    }

    static class VaultUnsealStatusImpl
    implements VaultUnsealStatus {
        private boolean sealed;
        @JsonProperty(value="t")
        private int secretThreshold;
        @JsonProperty(value="n")
        private int secretShares;
        private int progress;

        @Override
        public boolean isSealed() {
            return this.sealed;
        }

        @Override
        public int getSecretThreshold() {
            return this.secretThreshold;
        }

        @Override
        public int getSecretShares() {
            return this.secretShares;
        }

        @Override
        public int getProgress() {
            return this.progress;
        }

        public void setSealed(boolean sealed) {
            this.sealed = sealed;
        }

        public void setSecretThreshold(int secretThreshold) {
            this.secretThreshold = secretThreshold;
        }

        public void setSecretShares(int secretShares) {
            this.secretShares = secretShares;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VaultUnsealStatusImpl)) {
                return false;
            }
            VaultUnsealStatusImpl that = (VaultUnsealStatusImpl)o;
            return this.sealed == that.sealed && this.secretThreshold == that.secretThreshold && this.secretShares == that.secretShares && this.progress == that.progress;
        }

        public int hashCode() {
            return Objects.hash(this.sealed, this.secretThreshold, this.secretShares, this.progress);
        }
    }

    static class VaultInitializationResponseImpl
    implements VaultInitializationResponse {
        private List<String> keys = new ArrayList<String>();
        @JsonProperty(value="root_token")
        private String rootToken = "";

        @Override
        public VaultToken getRootToken() {
            return VaultToken.of(this.rootToken);
        }

        @Override
        public List<String> getKeys() {
            return this.keys;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys;
        }

        public void setRootToken(String rootToken) {
            this.rootToken = rootToken;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VaultInitializationResponseImpl)) {
                return false;
            }
            VaultInitializationResponseImpl that = (VaultInitializationResponseImpl)o;
            return this.keys.equals(that.keys) && this.rootToken.equals(that.rootToken);
        }

        public int hashCode() {
            return Objects.hash(this.keys, this.rootToken);
        }
    }

    private static class Health
    implements RestOperationsCallback<VaultHealth> {
        private Health() {
        }

        @Override
        public VaultHealth doWithRestOperations(RestOperations restOperations) {
            try {
                ResponseEntity<VaultHealthImpl> healthResponse = restOperations.exchange("sys/health", HttpMethod.GET, VaultSysTemplate.emptyNamespace(null), VaultHealthImpl.class, new Object[0]);
                return (VaultHealth)healthResponse.getBody();
            }
            catch (RestClientResponseException responseError) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(responseError.getResponseBodyAsString(), VaultHealthImpl.class);
                }
                catch (Exception jsonError) {
                    throw responseError;
                }
            }
        }
    }

    private static class GetMounts
    implements RestOperationsCallback<Map<String, VaultMount>> {
        private static final ParameterizedTypeReference<VaultMountsResponse> MOUNT_TYPE_REF = new ParameterizedTypeReference<VaultMountsResponse>(){};
        private final String path;

        GetMounts(String path) {
            this.path = path;
        }

        @Override
        public Map<String, VaultMount> doWithRestOperations(RestOperations restOperations) {
            ResponseEntity<VaultMountsResponse> exchange2 = restOperations.exchange(this.path, HttpMethod.GET, null, MOUNT_TYPE_REF, Collections.emptyMap());
            VaultMountsResponse body = (VaultMountsResponse)exchange2.getBody();
            Assert.state(body != null, "Get mounts response must not be null");
            if (body.getData() != null) {
                return (Map)body.getData();
            }
            return body.getTopLevelMounts();
        }

        private static class VaultMountsResponse
        extends VaultResponseSupport<Map<String, VaultMount>> {
            private Map<String, VaultMount> topLevelMounts = new HashMap<String, VaultMount>();

            private VaultMountsResponse() {
            }

            @JsonIgnore
            public Map<String, VaultMount> getTopLevelMounts() {
                return this.topLevelMounts;
            }

            @JsonAnySetter
            public void set(String name, Object value) {
                if (!(value instanceof Map)) {
                    return;
                }
                Map map = (Map)value;
                if (map.containsKey("type")) {
                    VaultMount.VaultMountBuilder builder = VaultMount.builder().type((String)map.get("type")).description((String)map.get("description"));
                    if (map.containsKey("config")) {
                        builder.config((Map)map.get("config"));
                    }
                    VaultMount vaultMount = builder.build();
                    this.topLevelMounts.put(name, vaultMount);
                }
            }
        }
    }

    private static class Seal
    implements RestOperationsCallback<Void> {
        private Seal() {
        }

        @Override
        public Void doWithRestOperations(RestOperations restOperations) {
            restOperations.put("sys/seal", null, new Object[0]);
            return null;
        }
    }

    private static class GetUnsealStatus
    implements RestOperationsCallback<VaultUnsealStatus> {
        private GetUnsealStatus() {
        }

        @Override
        public VaultUnsealStatus doWithRestOperations(RestOperations restOperations) {
            return restOperations.getForObject("sys/seal-status", VaultUnsealStatusImpl.class, new Object[0]);
        }
    }
}

