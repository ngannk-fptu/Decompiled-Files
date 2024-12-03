/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.Base64Utils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Hmac;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.RawTransitKey;
import org.springframework.vault.support.Signature;
import org.springframework.vault.support.SignatureValidation;
import org.springframework.vault.support.TransitKeyType;
import org.springframework.vault.support.VaultDecryptionResult;
import org.springframework.vault.support.VaultEncryptionResult;
import org.springframework.vault.support.VaultHmacRequest;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultSignRequest;
import org.springframework.vault.support.VaultSignatureVerificationRequest;
import org.springframework.vault.support.VaultTransitContext;
import org.springframework.vault.support.VaultTransitKey;
import org.springframework.vault.support.VaultTransitKeyConfiguration;
import org.springframework.vault.support.VaultTransitKeyCreationRequest;

public class VaultTransitTemplate
implements VaultTransitOperations {
    private final VaultOperations vaultOperations;
    private final String path;

    public VaultTransitTemplate(VaultOperations vaultOperations, String path) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations = vaultOperations;
        this.path = path;
    }

    @Override
    public void createKey(String keyName) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        this.vaultOperations.write(String.format("%s/keys/%s", this.path, keyName), null);
    }

    @Override
    public void createKey(String keyName, VaultTransitKeyCreationRequest createKeyRequest) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)createKeyRequest, (String)"VaultTransitKeyCreationRequest must not be empty");
        this.vaultOperations.write(String.format("%s/keys/%s", this.path, keyName), createKeyRequest);
    }

    @Override
    public List<String> getKeys() {
        VaultResponse response = this.vaultOperations.read(String.format("%s/keys?list=true", this.path));
        return response == null ? Collections.emptyList() : (List)((Map)response.getRequiredData()).get("keys");
    }

    @Override
    public void configureKey(String keyName, VaultTransitKeyConfiguration keyConfiguration) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)keyConfiguration, (String)"VaultKeyConfiguration must not be empty");
        this.vaultOperations.write(String.format("%s/keys/%s/config", this.path, keyName), keyConfiguration);
    }

    @Override
    @Nullable
    public RawTransitKey exportKey(String keyName, TransitKeyType type) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)((Object)type), (String)"Key type must not be null");
        VaultResponseSupport<RawTransitKeyImpl> result = this.vaultOperations.read(String.format("%s/export/%s/%s", this.path, type.getValue(), keyName), RawTransitKeyImpl.class);
        return result != null ? (RawTransitKey)result.getRequiredData() : null;
    }

    @Override
    @Nullable
    public VaultTransitKey getKey(String keyName) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        VaultResponseSupport<VaultTransitKeyImpl> result = this.vaultOperations.read(String.format("%s/keys/%s", this.path, keyName), VaultTransitKeyImpl.class);
        if (result != null) {
            return result.getRequiredData();
        }
        return null;
    }

    @Override
    public void deleteKey(String keyName) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        this.vaultOperations.delete(String.format("%s/keys/%s", this.path, keyName));
    }

    @Override
    public void rotate(String keyName) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        this.vaultOperations.write(String.format("%s/keys/%s/rotate", this.path, keyName), null);
    }

    @Override
    public String encrypt(String keyName, String plaintext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("plaintext", Base64Utils.encodeToString((byte[])plaintext.getBytes()));
        return (String)((Map)this.vaultOperations.write(String.format("%s/encrypt/%s", this.path, keyName), request).getRequiredData()).get("ciphertext");
    }

    @Override
    public Ciphertext encrypt(String keyName, Plaintext plaintext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        String ciphertext = this.encrypt(keyName, plaintext.getPlaintext(), plaintext.getContext());
        return VaultTransitTemplate.toCiphertext(ciphertext, plaintext.getContext());
    }

    @Override
    public String encrypt(String keyName, byte[] plaintext, VaultTransitContext transitContext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        Assert.notNull((Object)transitContext, (String)"VaultTransitContext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("plaintext", Base64Utils.encodeToString((byte[])plaintext));
        VaultTransitTemplate.applyTransitOptions(transitContext, request);
        return (String)((Map)this.vaultOperations.write(String.format("%s/encrypt/%s", this.path, keyName), request).getRequiredData()).get("ciphertext");
    }

    @Override
    public List<VaultEncryptionResult> encrypt(String keyName, List<Plaintext> batchRequest) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notEmpty(batchRequest, (String)"BatchRequest must not be null and must have at least one entry");
        ArrayList<LinkedHashMap<String, String>> batch = new ArrayList<LinkedHashMap<String, String>>(batchRequest.size());
        for (Plaintext request : batchRequest) {
            LinkedHashMap<String, String> vaultRequest = new LinkedHashMap<String, String>(2);
            vaultRequest.put("plaintext", Base64Utils.encodeToString((byte[])request.getPlaintext()));
            if (request.getContext() != null) {
                VaultTransitTemplate.applyTransitOptions(request.getContext(), vaultRequest);
            }
            batch.add(vaultRequest);
        }
        VaultResponse vaultResponse = this.vaultOperations.write(String.format("%s/encrypt/%s", this.path, keyName), Collections.singletonMap("batch_input", batch));
        return VaultTransitTemplate.toEncryptionResults(vaultResponse, batchRequest);
    }

    @Override
    public String decrypt(String keyName, String ciphertext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be empty");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("ciphertext", ciphertext);
        String plaintext = (String)((Map)this.vaultOperations.write(String.format("%s/decrypt/%s", this.path, keyName), request).getRequiredData()).get("plaintext");
        return new String(Base64Utils.decodeFromString((String)plaintext));
    }

    @Override
    public Plaintext decrypt(String keyName, Ciphertext ciphertext) {
        Assert.hasText((String)keyName, (String)"Key name must not be null");
        Assert.notNull((Object)ciphertext, (String)"Ciphertext must not be null");
        byte[] plaintext = this.decrypt(keyName, ciphertext.getCiphertext(), ciphertext.getContext());
        return Plaintext.of(plaintext).with(ciphertext.getContext());
    }

    @Override
    public byte[] decrypt(String keyName, String ciphertext, VaultTransitContext transitContext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be empty");
        Assert.notNull((Object)transitContext, (String)"VaultTransitContext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("ciphertext", ciphertext);
        VaultTransitTemplate.applyTransitOptions(transitContext, request);
        String plaintext = (String)((Map)this.vaultOperations.write(String.format("%s/decrypt/%s", this.path, keyName), request).getRequiredData()).get("plaintext");
        return Base64Utils.decodeFromString((String)plaintext);
    }

    @Override
    public List<VaultDecryptionResult> decrypt(String keyName, List<Ciphertext> batchRequest) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notEmpty(batchRequest, (String)"BatchRequest must not be null and must have at least one entry");
        ArrayList<LinkedHashMap<String, String>> batch = new ArrayList<LinkedHashMap<String, String>>(batchRequest.size());
        for (Ciphertext request : batchRequest) {
            LinkedHashMap<String, String> vaultRequest = new LinkedHashMap<String, String>(2);
            vaultRequest.put("ciphertext", request.getCiphertext());
            if (request.getContext() != null) {
                VaultTransitTemplate.applyTransitOptions(request.getContext(), vaultRequest);
            }
            batch.add(vaultRequest);
        }
        VaultResponse vaultResponse = this.vaultOperations.write(String.format("%s/decrypt/%s", this.path, keyName), Collections.singletonMap("batch_input", batch));
        return VaultTransitTemplate.toDecryptionResults(vaultResponse, batchRequest);
    }

    @Override
    public String rewrap(String keyName, String ciphertext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be empty");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("ciphertext", ciphertext);
        return (String)((Map)this.vaultOperations.write(String.format("%s/rewrap/%s", this.path, keyName), request).getRequiredData()).get("ciphertext");
    }

    @Override
    public String rewrap(String keyName, String ciphertext, VaultTransitContext transitContext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.hasText((String)ciphertext, (String)"Ciphertext must not be empty");
        Assert.notNull((Object)transitContext, (String)"VaultTransitContext must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("ciphertext", ciphertext);
        VaultTransitTemplate.applyTransitOptions(transitContext, request);
        return (String)((Map)this.vaultOperations.write(String.format("%s/rewrap/%s", this.path, keyName), request).getRequiredData()).get("ciphertext");
    }

    @Override
    public Hmac getHmac(String keyName, Plaintext plaintext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        VaultHmacRequest request = VaultHmacRequest.create(plaintext);
        return this.getHmac(keyName, request);
    }

    @Override
    public Hmac getHmac(String keyName, VaultHmacRequest hmacRequest) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)hmacRequest, (String)"HMAC request must not be null");
        LinkedHashMap<String, Object> request = new LinkedHashMap<String, Object>();
        request.put("input", Base64Utils.encodeToString((byte[])hmacRequest.getPlaintext().getPlaintext()));
        if (StringUtils.hasText((String)hmacRequest.getAlgorithm())) {
            request.put("algorithm", hmacRequest.getAlgorithm());
        }
        if (hmacRequest.getKeyVersion() != null) {
            request.put("key_version ", hmacRequest.getKeyVersion());
        }
        String hmac = (String)((Map)this.vaultOperations.write(String.format("%s/hmac/%s", this.path, keyName), request).getRequiredData()).get("hmac");
        return Hmac.of(hmac);
    }

    @Override
    public Signature sign(String keyName, Plaintext plaintext) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plaintext, (String)"Plaintext must not be null");
        VaultSignRequest request = VaultSignRequest.create(plaintext);
        return this.sign(keyName, request);
    }

    @Override
    public Signature sign(String keyName, VaultSignRequest signRequest) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)signRequest, (String)"Sign request must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("input", Base64Utils.encodeToString((byte[])signRequest.getPlaintext().getPlaintext()));
        if (StringUtils.hasText((String)signRequest.getAlgorithm())) {
            request.put("algorithm", signRequest.getAlgorithm());
        }
        String signature = (String)((Map)this.vaultOperations.write(String.format("%s/sign/%s", this.path, keyName), request).getRequiredData()).get("signature");
        return Signature.of(signature);
    }

    @Override
    public boolean verify(String keyName, Plaintext plainText, Signature signature) {
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)plainText, (String)"Plaintext must not be null");
        Assert.notNull((Object)signature, (String)"Signature must not be null");
        VaultSignatureVerificationRequest request = VaultSignatureVerificationRequest.create(plainText, signature);
        return this.verify(keyName, request).isValid();
    }

    @Override
    public SignatureValidation verify(String keyName, VaultSignatureVerificationRequest verificationRequest) {
        Map response;
        Assert.hasText((String)keyName, (String)"Key name must not be empty");
        Assert.notNull((Object)verificationRequest, (String)"Signature verification request must not be null");
        LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
        request.put("input", Base64Utils.encodeToString((byte[])verificationRequest.getPlaintext().getPlaintext()));
        if (verificationRequest.getHmac() != null) {
            request.put("hmac", verificationRequest.getHmac().getHmac());
        }
        if (verificationRequest.getSignature() != null) {
            request.put("signature", verificationRequest.getSignature().getSignature());
        }
        if (StringUtils.hasText((String)verificationRequest.getAlgorithm())) {
            request.put("algorithm", verificationRequest.getAlgorithm());
        }
        if ((response = (Map)this.vaultOperations.write(String.format("%s/verify/%s", this.path, keyName), request).getRequiredData()).containsKey("valid") && Boolean.valueOf("" + response.get("valid")).booleanValue()) {
            return SignatureValidation.valid();
        }
        return SignatureValidation.invalid();
    }

    private static void applyTransitOptions(VaultTransitContext context, Map<String, String> request) {
        if (!ObjectUtils.isEmpty((Object)context.getContext())) {
            request.put("context", Base64Utils.encodeToString((byte[])context.getContext()));
        }
        if (!ObjectUtils.isEmpty((Object)context.getNonce())) {
            request.put("nonce", Base64Utils.encodeToString((byte[])context.getNonce()));
        }
    }

    private static List<VaultEncryptionResult> toEncryptionResults(VaultResponse vaultResponse, List<Plaintext> batchRequest) {
        ArrayList<VaultEncryptionResult> result = new ArrayList<VaultEncryptionResult>(batchRequest.size());
        List<Map<String, String>> batchData = VaultTransitTemplate.getBatchData(vaultResponse);
        for (int i = 0; i < batchRequest.size(); ++i) {
            Map<String, String> data;
            Plaintext plaintext = batchRequest.get(i);
            VaultEncryptionResult encrypted = batchData.size() > i ? (StringUtils.hasText((String)(data = batchData.get(i)).get("error")) ? new VaultEncryptionResult(new VaultException(data.get("error"))) : new VaultEncryptionResult(VaultTransitTemplate.toCiphertext(data.get("ciphertext"), plaintext.getContext()))) : new VaultEncryptionResult(new VaultException("No result for plaintext #" + i));
            result.add(encrypted);
        }
        return result;
    }

    private static List<VaultDecryptionResult> toDecryptionResults(VaultResponse vaultResponse, List<Ciphertext> batchRequest) {
        ArrayList<VaultDecryptionResult> result = new ArrayList<VaultDecryptionResult>(batchRequest.size());
        List<Map<String, String>> batchData = VaultTransitTemplate.getBatchData(vaultResponse);
        for (int i = 0; i < batchRequest.size(); ++i) {
            Ciphertext ciphertext = batchRequest.get(i);
            VaultDecryptionResult encrypted = batchData.size() > i ? VaultTransitTemplate.getDecryptionResult(batchData.get(i), ciphertext) : new VaultDecryptionResult(new VaultException("No result for ciphertext #" + i));
            result.add(encrypted);
        }
        return result;
    }

    private static VaultDecryptionResult getDecryptionResult(Map<String, String> data, Ciphertext ciphertext) {
        if (StringUtils.hasText((String)data.get("error"))) {
            return new VaultDecryptionResult(new VaultException(data.get("error")));
        }
        if (StringUtils.hasText((String)data.get("plaintext"))) {
            byte[] plaintext = Base64Utils.decodeFromString((String)data.get("plaintext"));
            return new VaultDecryptionResult(Plaintext.of(plaintext).with(ciphertext.getContext()));
        }
        return new VaultDecryptionResult(Plaintext.empty().with(ciphertext.getContext()));
    }

    private static Ciphertext toCiphertext(String ciphertext, @Nullable VaultTransitContext context) {
        return context != null ? Ciphertext.of(ciphertext).with(context) : Ciphertext.of(ciphertext);
    }

    private static List<Map<String, String>> getBatchData(VaultResponse vaultResponse) {
        return (List)((Map)vaultResponse.getRequiredData()).get("batch_results");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [vaultOperations=").append(this.vaultOperations);
        sb.append(", path='").append(this.path).append('\'');
        sb.append(']');
        return sb.toString();
    }

    static class RawTransitKeyImpl
    implements RawTransitKey {
        private Map<String, String> keys = Collections.emptyMap();
        @Nullable
        private String name;

        @Override
        public Map<String, String> getKeys() {
            return this.keys;
        }

        @Override
        @Nullable
        public String getName() {
            return this.name;
        }

        public void setKeys(Map<String, String> keys) {
            this.keys = keys;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RawTransitKeyImpl)) {
                return false;
            }
            RawTransitKeyImpl that = (RawTransitKeyImpl)o;
            return this.keys.equals(that.keys) && Objects.equals(this.name, that.name);
        }

        public int hashCode() {
            return Objects.hash(this.keys, this.name);
        }
    }

    static class VaultTransitKeyImpl
    implements VaultTransitKey {
        @Nullable
        private String name;
        @JsonProperty(value="cipher_mode")
        private String cipherMode = "";
        @JsonProperty(value="type")
        @Nullable
        private String type;
        @JsonProperty(value="deletion_allowed")
        private boolean deletionAllowed;
        private boolean derived;
        private boolean exportable;
        private Map<String, Object> keys = Collections.emptyMap();
        @JsonProperty(value="latest_version")
        private int latestVersion;
        @JsonProperty(value="min_decryption_version")
        private int minDecryptionVersion;
        @JsonProperty(value="min_encryption_version")
        private int minEncryptionVersion;
        @JsonProperty(value="supports_decryption")
        private boolean supportsDecryption;
        @JsonProperty(value="supports_encryption")
        private boolean supportsEncryption;
        @JsonProperty(value="supports_derivation")
        private boolean supportsDerivation;
        @JsonProperty(value="supports_signing")
        private boolean supportsSigning;

        @Override
        public String getType() {
            if (this.type != null) {
                return this.type;
            }
            return this.cipherMode;
        }

        @Override
        public boolean supportsDecryption() {
            return this.isSupportsDecryption();
        }

        @Override
        public boolean supportsEncryption() {
            return this.isSupportsEncryption();
        }

        @Override
        public boolean supportsDerivation() {
            return this.isSupportsDerivation();
        }

        @Override
        public boolean supportsSigning() {
            return this.isSupportsSigning();
        }

        @Override
        @Nullable
        public String getName() {
            return this.name;
        }

        public String getCipherMode() {
            return this.cipherMode;
        }

        @Override
        public boolean isDeletionAllowed() {
            return this.deletionAllowed;
        }

        @Override
        public boolean isDerived() {
            return this.derived;
        }

        @Override
        public boolean isExportable() {
            return this.exportable;
        }

        @Override
        public Map<String, Object> getKeys() {
            return this.keys;
        }

        @Override
        public int getLatestVersion() {
            return this.latestVersion;
        }

        @Override
        public int getMinDecryptionVersion() {
            return this.minDecryptionVersion;
        }

        @Override
        public int getMinEncryptionVersion() {
            return this.minEncryptionVersion;
        }

        public boolean isSupportsDecryption() {
            return this.supportsDecryption;
        }

        public boolean isSupportsEncryption() {
            return this.supportsEncryption;
        }

        public boolean isSupportsDerivation() {
            return this.supportsDerivation;
        }

        public boolean isSupportsSigning() {
            return this.supportsSigning;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        public void setCipherMode(String cipherMode) {
            this.cipherMode = cipherMode;
        }

        public void setType(@Nullable String type) {
            this.type = type;
        }

        public void setDeletionAllowed(boolean deletionAllowed) {
            this.deletionAllowed = deletionAllowed;
        }

        public void setDerived(boolean derived) {
            this.derived = derived;
        }

        public void setExportable(boolean exportable) {
            this.exportable = exportable;
        }

        public void setKeys(Map<String, Object> keys) {
            this.keys = keys;
        }

        public void setLatestVersion(int latestVersion) {
            this.latestVersion = latestVersion;
        }

        public void setMinDecryptionVersion(int minDecryptionVersion) {
            this.minDecryptionVersion = minDecryptionVersion;
        }

        public void setMinEncryptionVersion(int minEncryptionVersion) {
            this.minEncryptionVersion = minEncryptionVersion;
        }

        public void setSupportsDecryption(boolean supportsDecryption) {
            this.supportsDecryption = supportsDecryption;
        }

        public void setSupportsEncryption(boolean supportsEncryption) {
            this.supportsEncryption = supportsEncryption;
        }

        public void setSupportsDerivation(boolean supportsDerivation) {
            this.supportsDerivation = supportsDerivation;
        }

        public void setSupportsSigning(boolean supportsSigning) {
            this.supportsSigning = supportsSigning;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VaultTransitKeyImpl)) {
                return false;
            }
            VaultTransitKeyImpl that = (VaultTransitKeyImpl)o;
            return this.deletionAllowed == that.deletionAllowed && this.derived == that.derived && this.exportable == that.exportable && this.latestVersion == that.latestVersion && this.minDecryptionVersion == that.minDecryptionVersion && this.minEncryptionVersion == that.minEncryptionVersion && this.supportsDecryption == that.supportsDecryption && this.supportsEncryption == that.supportsEncryption && this.supportsDerivation == that.supportsDerivation && this.supportsSigning == that.supportsSigning && Objects.equals(this.name, that.name) && this.cipherMode.equals(that.cipherMode) && Objects.equals(this.type, that.type) && this.keys.equals(that.keys);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.cipherMode, this.type, this.deletionAllowed, this.derived, this.exportable, this.keys, this.latestVersion, this.minDecryptionVersion, this.minEncryptionVersion, this.supportsDecryption, this.supportsEncryption, this.supportsDerivation, this.supportsSigning);
        }
    }
}

