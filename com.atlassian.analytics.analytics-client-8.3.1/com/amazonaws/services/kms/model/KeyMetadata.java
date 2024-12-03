/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.CustomerMasterKeySpec;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import com.amazonaws.services.kms.model.ExpirationModelType;
import com.amazonaws.services.kms.model.KeyManagerType;
import com.amazonaws.services.kms.model.KeySpec;
import com.amazonaws.services.kms.model.KeyState;
import com.amazonaws.services.kms.model.KeyUsageType;
import com.amazonaws.services.kms.model.MacAlgorithmSpec;
import com.amazonaws.services.kms.model.MultiRegionConfiguration;
import com.amazonaws.services.kms.model.OriginType;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import com.amazonaws.services.kms.model.transform.KeyMetadataMarshaller;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class KeyMetadata
implements Serializable,
Cloneable,
StructuredPojo {
    private String aWSAccountId;
    private String keyId;
    private String arn;
    private Date creationDate;
    private Boolean enabled;
    private String description;
    private String keyUsage;
    private String keyState;
    private Date deletionDate;
    private Date validTo;
    private String origin;
    private String customKeyStoreId;
    private String cloudHsmClusterId;
    private String expirationModel;
    private String keyManager;
    @Deprecated
    private String customerMasterKeySpec;
    private String keySpec;
    private SdkInternalList<String> encryptionAlgorithms;
    private SdkInternalList<String> signingAlgorithms;
    private Boolean multiRegion;
    private MultiRegionConfiguration multiRegionConfiguration;
    private Integer pendingDeletionWindowInDays;
    private SdkInternalList<String> macAlgorithms;

    public void setAWSAccountId(String aWSAccountId) {
        this.aWSAccountId = aWSAccountId;
    }

    public String getAWSAccountId() {
        return this.aWSAccountId;
    }

    public KeyMetadata withAWSAccountId(String aWSAccountId) {
        this.setAWSAccountId(aWSAccountId);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public KeyMetadata withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getArn() {
        return this.arn;
    }

    public KeyMetadata withArn(String arn) {
        this.setArn(arn);
        return this;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public KeyMetadata withCreationDate(Date creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public KeyMetadata withEnabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public KeyMetadata withDescription(String description) {
        this.setDescription(description);
        return this;
    }

    public void setKeyUsage(String keyUsage) {
        this.keyUsage = keyUsage;
    }

    public String getKeyUsage() {
        return this.keyUsage;
    }

    public KeyMetadata withKeyUsage(String keyUsage) {
        this.setKeyUsage(keyUsage);
        return this;
    }

    public void setKeyUsage(KeyUsageType keyUsage) {
        this.withKeyUsage(keyUsage);
    }

    public KeyMetadata withKeyUsage(KeyUsageType keyUsage) {
        this.keyUsage = keyUsage.toString();
        return this;
    }

    public void setKeyState(String keyState) {
        this.keyState = keyState;
    }

    public String getKeyState() {
        return this.keyState;
    }

    public KeyMetadata withKeyState(String keyState) {
        this.setKeyState(keyState);
        return this;
    }

    public void setKeyState(KeyState keyState) {
        this.withKeyState(keyState);
    }

    public KeyMetadata withKeyState(KeyState keyState) {
        this.keyState = keyState.toString();
        return this;
    }

    public void setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
    }

    public Date getDeletionDate() {
        return this.deletionDate;
    }

    public KeyMetadata withDeletionDate(Date deletionDate) {
        this.setDeletionDate(deletionDate);
        return this;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public KeyMetadata withValidTo(Date validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrigin() {
        return this.origin;
    }

    public KeyMetadata withOrigin(String origin) {
        this.setOrigin(origin);
        return this;
    }

    public void setOrigin(OriginType origin) {
        this.withOrigin(origin);
    }

    public KeyMetadata withOrigin(OriginType origin) {
        this.origin = origin.toString();
        return this;
    }

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public KeyMetadata withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public void setCloudHsmClusterId(String cloudHsmClusterId) {
        this.cloudHsmClusterId = cloudHsmClusterId;
    }

    public String getCloudHsmClusterId() {
        return this.cloudHsmClusterId;
    }

    public KeyMetadata withCloudHsmClusterId(String cloudHsmClusterId) {
        this.setCloudHsmClusterId(cloudHsmClusterId);
        return this;
    }

    public void setExpirationModel(String expirationModel) {
        this.expirationModel = expirationModel;
    }

    public String getExpirationModel() {
        return this.expirationModel;
    }

    public KeyMetadata withExpirationModel(String expirationModel) {
        this.setExpirationModel(expirationModel);
        return this;
    }

    public void setExpirationModel(ExpirationModelType expirationModel) {
        this.withExpirationModel(expirationModel);
    }

    public KeyMetadata withExpirationModel(ExpirationModelType expirationModel) {
        this.expirationModel = expirationModel.toString();
        return this;
    }

    public void setKeyManager(String keyManager) {
        this.keyManager = keyManager;
    }

    public String getKeyManager() {
        return this.keyManager;
    }

    public KeyMetadata withKeyManager(String keyManager) {
        this.setKeyManager(keyManager);
        return this;
    }

    public void setKeyManager(KeyManagerType keyManager) {
        this.withKeyManager(keyManager);
    }

    public KeyMetadata withKeyManager(KeyManagerType keyManager) {
        this.keyManager = keyManager.toString();
        return this;
    }

    @Deprecated
    public void setCustomerMasterKeySpec(String customerMasterKeySpec) {
        this.customerMasterKeySpec = customerMasterKeySpec;
    }

    @Deprecated
    public String getCustomerMasterKeySpec() {
        return this.customerMasterKeySpec;
    }

    @Deprecated
    public KeyMetadata withCustomerMasterKeySpec(String customerMasterKeySpec) {
        this.setCustomerMasterKeySpec(customerMasterKeySpec);
        return this;
    }

    @Deprecated
    public void setCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
        this.withCustomerMasterKeySpec(customerMasterKeySpec);
    }

    @Deprecated
    public KeyMetadata withCustomerMasterKeySpec(CustomerMasterKeySpec customerMasterKeySpec) {
        this.customerMasterKeySpec = customerMasterKeySpec.toString();
        return this;
    }

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return this.keySpec;
    }

    public KeyMetadata withKeySpec(String keySpec) {
        this.setKeySpec(keySpec);
        return this;
    }

    public void setKeySpec(KeySpec keySpec) {
        this.withKeySpec(keySpec);
    }

    public KeyMetadata withKeySpec(KeySpec keySpec) {
        this.keySpec = keySpec.toString();
        return this;
    }

    public List<String> getEncryptionAlgorithms() {
        if (this.encryptionAlgorithms == null) {
            this.encryptionAlgorithms = new SdkInternalList();
        }
        return this.encryptionAlgorithms;
    }

    public void setEncryptionAlgorithms(Collection<String> encryptionAlgorithms) {
        if (encryptionAlgorithms == null) {
            this.encryptionAlgorithms = null;
            return;
        }
        this.encryptionAlgorithms = new SdkInternalList<String>(encryptionAlgorithms);
    }

    public KeyMetadata withEncryptionAlgorithms(String ... encryptionAlgorithms) {
        if (this.encryptionAlgorithms == null) {
            this.setEncryptionAlgorithms(new SdkInternalList<String>(encryptionAlgorithms.length));
        }
        for (String ele : encryptionAlgorithms) {
            this.encryptionAlgorithms.add(ele);
        }
        return this;
    }

    public KeyMetadata withEncryptionAlgorithms(Collection<String> encryptionAlgorithms) {
        this.setEncryptionAlgorithms(encryptionAlgorithms);
        return this;
    }

    public KeyMetadata withEncryptionAlgorithms(EncryptionAlgorithmSpec ... encryptionAlgorithms) {
        SdkInternalList<String> encryptionAlgorithmsCopy = new SdkInternalList<String>(encryptionAlgorithms.length);
        for (EncryptionAlgorithmSpec value : encryptionAlgorithms) {
            encryptionAlgorithmsCopy.add(value.toString());
        }
        if (this.getEncryptionAlgorithms() == null) {
            this.setEncryptionAlgorithms(encryptionAlgorithmsCopy);
        } else {
            this.getEncryptionAlgorithms().addAll(encryptionAlgorithmsCopy);
        }
        return this;
    }

    public List<String> getSigningAlgorithms() {
        if (this.signingAlgorithms == null) {
            this.signingAlgorithms = new SdkInternalList();
        }
        return this.signingAlgorithms;
    }

    public void setSigningAlgorithms(Collection<String> signingAlgorithms) {
        if (signingAlgorithms == null) {
            this.signingAlgorithms = null;
            return;
        }
        this.signingAlgorithms = new SdkInternalList<String>(signingAlgorithms);
    }

    public KeyMetadata withSigningAlgorithms(String ... signingAlgorithms) {
        if (this.signingAlgorithms == null) {
            this.setSigningAlgorithms(new SdkInternalList<String>(signingAlgorithms.length));
        }
        for (String ele : signingAlgorithms) {
            this.signingAlgorithms.add(ele);
        }
        return this;
    }

    public KeyMetadata withSigningAlgorithms(Collection<String> signingAlgorithms) {
        this.setSigningAlgorithms(signingAlgorithms);
        return this;
    }

    public KeyMetadata withSigningAlgorithms(SigningAlgorithmSpec ... signingAlgorithms) {
        SdkInternalList<String> signingAlgorithmsCopy = new SdkInternalList<String>(signingAlgorithms.length);
        for (SigningAlgorithmSpec value : signingAlgorithms) {
            signingAlgorithmsCopy.add(value.toString());
        }
        if (this.getSigningAlgorithms() == null) {
            this.setSigningAlgorithms(signingAlgorithmsCopy);
        } else {
            this.getSigningAlgorithms().addAll(signingAlgorithmsCopy);
        }
        return this;
    }

    public void setMultiRegion(Boolean multiRegion) {
        this.multiRegion = multiRegion;
    }

    public Boolean getMultiRegion() {
        return this.multiRegion;
    }

    public KeyMetadata withMultiRegion(Boolean multiRegion) {
        this.setMultiRegion(multiRegion);
        return this;
    }

    public Boolean isMultiRegion() {
        return this.multiRegion;
    }

    public void setMultiRegionConfiguration(MultiRegionConfiguration multiRegionConfiguration) {
        this.multiRegionConfiguration = multiRegionConfiguration;
    }

    public MultiRegionConfiguration getMultiRegionConfiguration() {
        return this.multiRegionConfiguration;
    }

    public KeyMetadata withMultiRegionConfiguration(MultiRegionConfiguration multiRegionConfiguration) {
        this.setMultiRegionConfiguration(multiRegionConfiguration);
        return this;
    }

    public void setPendingDeletionWindowInDays(Integer pendingDeletionWindowInDays) {
        this.pendingDeletionWindowInDays = pendingDeletionWindowInDays;
    }

    public Integer getPendingDeletionWindowInDays() {
        return this.pendingDeletionWindowInDays;
    }

    public KeyMetadata withPendingDeletionWindowInDays(Integer pendingDeletionWindowInDays) {
        this.setPendingDeletionWindowInDays(pendingDeletionWindowInDays);
        return this;
    }

    public List<String> getMacAlgorithms() {
        if (this.macAlgorithms == null) {
            this.macAlgorithms = new SdkInternalList();
        }
        return this.macAlgorithms;
    }

    public void setMacAlgorithms(Collection<String> macAlgorithms) {
        if (macAlgorithms == null) {
            this.macAlgorithms = null;
            return;
        }
        this.macAlgorithms = new SdkInternalList<String>(macAlgorithms);
    }

    public KeyMetadata withMacAlgorithms(String ... macAlgorithms) {
        if (this.macAlgorithms == null) {
            this.setMacAlgorithms(new SdkInternalList<String>(macAlgorithms.length));
        }
        for (String ele : macAlgorithms) {
            this.macAlgorithms.add(ele);
        }
        return this;
    }

    public KeyMetadata withMacAlgorithms(Collection<String> macAlgorithms) {
        this.setMacAlgorithms(macAlgorithms);
        return this;
    }

    public KeyMetadata withMacAlgorithms(MacAlgorithmSpec ... macAlgorithms) {
        SdkInternalList<String> macAlgorithmsCopy = new SdkInternalList<String>(macAlgorithms.length);
        for (MacAlgorithmSpec value : macAlgorithms) {
            macAlgorithmsCopy.add(value.toString());
        }
        if (this.getMacAlgorithms() == null) {
            this.setMacAlgorithms(macAlgorithmsCopy);
        } else {
            this.getMacAlgorithms().addAll(macAlgorithmsCopy);
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getAWSAccountId() != null) {
            sb.append("AWSAccountId: ").append(this.getAWSAccountId()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getArn() != null) {
            sb.append("Arn: ").append(this.getArn()).append(",");
        }
        if (this.getCreationDate() != null) {
            sb.append("CreationDate: ").append(this.getCreationDate()).append(",");
        }
        if (this.getEnabled() != null) {
            sb.append("Enabled: ").append(this.getEnabled()).append(",");
        }
        if (this.getDescription() != null) {
            sb.append("Description: ").append(this.getDescription()).append(",");
        }
        if (this.getKeyUsage() != null) {
            sb.append("KeyUsage: ").append(this.getKeyUsage()).append(",");
        }
        if (this.getKeyState() != null) {
            sb.append("KeyState: ").append(this.getKeyState()).append(",");
        }
        if (this.getDeletionDate() != null) {
            sb.append("DeletionDate: ").append(this.getDeletionDate()).append(",");
        }
        if (this.getValidTo() != null) {
            sb.append("ValidTo: ").append(this.getValidTo()).append(",");
        }
        if (this.getOrigin() != null) {
            sb.append("Origin: ").append(this.getOrigin()).append(",");
        }
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId()).append(",");
        }
        if (this.getCloudHsmClusterId() != null) {
            sb.append("CloudHsmClusterId: ").append(this.getCloudHsmClusterId()).append(",");
        }
        if (this.getExpirationModel() != null) {
            sb.append("ExpirationModel: ").append(this.getExpirationModel()).append(",");
        }
        if (this.getKeyManager() != null) {
            sb.append("KeyManager: ").append(this.getKeyManager()).append(",");
        }
        if (this.getCustomerMasterKeySpec() != null) {
            sb.append("CustomerMasterKeySpec: ").append(this.getCustomerMasterKeySpec()).append(",");
        }
        if (this.getKeySpec() != null) {
            sb.append("KeySpec: ").append(this.getKeySpec()).append(",");
        }
        if (this.getEncryptionAlgorithms() != null) {
            sb.append("EncryptionAlgorithms: ").append(this.getEncryptionAlgorithms()).append(",");
        }
        if (this.getSigningAlgorithms() != null) {
            sb.append("SigningAlgorithms: ").append(this.getSigningAlgorithms()).append(",");
        }
        if (this.getMultiRegion() != null) {
            sb.append("MultiRegion: ").append(this.getMultiRegion()).append(",");
        }
        if (this.getMultiRegionConfiguration() != null) {
            sb.append("MultiRegionConfiguration: ").append(this.getMultiRegionConfiguration()).append(",");
        }
        if (this.getPendingDeletionWindowInDays() != null) {
            sb.append("PendingDeletionWindowInDays: ").append(this.getPendingDeletionWindowInDays()).append(",");
        }
        if (this.getMacAlgorithms() != null) {
            sb.append("MacAlgorithms: ").append(this.getMacAlgorithms());
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof KeyMetadata)) {
            return false;
        }
        KeyMetadata other = (KeyMetadata)obj;
        if (other.getAWSAccountId() == null ^ this.getAWSAccountId() == null) {
            return false;
        }
        if (other.getAWSAccountId() != null && !other.getAWSAccountId().equals(this.getAWSAccountId())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getArn() == null ^ this.getArn() == null) {
            return false;
        }
        if (other.getArn() != null && !other.getArn().equals(this.getArn())) {
            return false;
        }
        if (other.getCreationDate() == null ^ this.getCreationDate() == null) {
            return false;
        }
        if (other.getCreationDate() != null && !other.getCreationDate().equals(this.getCreationDate())) {
            return false;
        }
        if (other.getEnabled() == null ^ this.getEnabled() == null) {
            return false;
        }
        if (other.getEnabled() != null && !other.getEnabled().equals(this.getEnabled())) {
            return false;
        }
        if (other.getDescription() == null ^ this.getDescription() == null) {
            return false;
        }
        if (other.getDescription() != null && !other.getDescription().equals(this.getDescription())) {
            return false;
        }
        if (other.getKeyUsage() == null ^ this.getKeyUsage() == null) {
            return false;
        }
        if (other.getKeyUsage() != null && !other.getKeyUsage().equals(this.getKeyUsage())) {
            return false;
        }
        if (other.getKeyState() == null ^ this.getKeyState() == null) {
            return false;
        }
        if (other.getKeyState() != null && !other.getKeyState().equals(this.getKeyState())) {
            return false;
        }
        if (other.getDeletionDate() == null ^ this.getDeletionDate() == null) {
            return false;
        }
        if (other.getDeletionDate() != null && !other.getDeletionDate().equals(this.getDeletionDate())) {
            return false;
        }
        if (other.getValidTo() == null ^ this.getValidTo() == null) {
            return false;
        }
        if (other.getValidTo() != null && !other.getValidTo().equals(this.getValidTo())) {
            return false;
        }
        if (other.getOrigin() == null ^ this.getOrigin() == null) {
            return false;
        }
        if (other.getOrigin() != null && !other.getOrigin().equals(this.getOrigin())) {
            return false;
        }
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        if (other.getCustomKeyStoreId() != null && !other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId())) {
            return false;
        }
        if (other.getCloudHsmClusterId() == null ^ this.getCloudHsmClusterId() == null) {
            return false;
        }
        if (other.getCloudHsmClusterId() != null && !other.getCloudHsmClusterId().equals(this.getCloudHsmClusterId())) {
            return false;
        }
        if (other.getExpirationModel() == null ^ this.getExpirationModel() == null) {
            return false;
        }
        if (other.getExpirationModel() != null && !other.getExpirationModel().equals(this.getExpirationModel())) {
            return false;
        }
        if (other.getKeyManager() == null ^ this.getKeyManager() == null) {
            return false;
        }
        if (other.getKeyManager() != null && !other.getKeyManager().equals(this.getKeyManager())) {
            return false;
        }
        if (other.getCustomerMasterKeySpec() == null ^ this.getCustomerMasterKeySpec() == null) {
            return false;
        }
        if (other.getCustomerMasterKeySpec() != null && !other.getCustomerMasterKeySpec().equals(this.getCustomerMasterKeySpec())) {
            return false;
        }
        if (other.getKeySpec() == null ^ this.getKeySpec() == null) {
            return false;
        }
        if (other.getKeySpec() != null && !other.getKeySpec().equals(this.getKeySpec())) {
            return false;
        }
        if (other.getEncryptionAlgorithms() == null ^ this.getEncryptionAlgorithms() == null) {
            return false;
        }
        if (other.getEncryptionAlgorithms() != null && !other.getEncryptionAlgorithms().equals(this.getEncryptionAlgorithms())) {
            return false;
        }
        if (other.getSigningAlgorithms() == null ^ this.getSigningAlgorithms() == null) {
            return false;
        }
        if (other.getSigningAlgorithms() != null && !other.getSigningAlgorithms().equals(this.getSigningAlgorithms())) {
            return false;
        }
        if (other.getMultiRegion() == null ^ this.getMultiRegion() == null) {
            return false;
        }
        if (other.getMultiRegion() != null && !other.getMultiRegion().equals(this.getMultiRegion())) {
            return false;
        }
        if (other.getMultiRegionConfiguration() == null ^ this.getMultiRegionConfiguration() == null) {
            return false;
        }
        if (other.getMultiRegionConfiguration() != null && !other.getMultiRegionConfiguration().equals(this.getMultiRegionConfiguration())) {
            return false;
        }
        if (other.getPendingDeletionWindowInDays() == null ^ this.getPendingDeletionWindowInDays() == null) {
            return false;
        }
        if (other.getPendingDeletionWindowInDays() != null && !other.getPendingDeletionWindowInDays().equals(this.getPendingDeletionWindowInDays())) {
            return false;
        }
        if (other.getMacAlgorithms() == null ^ this.getMacAlgorithms() == null) {
            return false;
        }
        return other.getMacAlgorithms() == null || other.getMacAlgorithms().equals(this.getMacAlgorithms());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getAWSAccountId() == null ? 0 : this.getAWSAccountId().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getArn() == null ? 0 : this.getArn().hashCode());
        hashCode = 31 * hashCode + (this.getCreationDate() == null ? 0 : this.getCreationDate().hashCode());
        hashCode = 31 * hashCode + (this.getEnabled() == null ? 0 : this.getEnabled().hashCode());
        hashCode = 31 * hashCode + (this.getDescription() == null ? 0 : this.getDescription().hashCode());
        hashCode = 31 * hashCode + (this.getKeyUsage() == null ? 0 : this.getKeyUsage().hashCode());
        hashCode = 31 * hashCode + (this.getKeyState() == null ? 0 : this.getKeyState().hashCode());
        hashCode = 31 * hashCode + (this.getDeletionDate() == null ? 0 : this.getDeletionDate().hashCode());
        hashCode = 31 * hashCode + (this.getValidTo() == null ? 0 : this.getValidTo().hashCode());
        hashCode = 31 * hashCode + (this.getOrigin() == null ? 0 : this.getOrigin().hashCode());
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        hashCode = 31 * hashCode + (this.getCloudHsmClusterId() == null ? 0 : this.getCloudHsmClusterId().hashCode());
        hashCode = 31 * hashCode + (this.getExpirationModel() == null ? 0 : this.getExpirationModel().hashCode());
        hashCode = 31 * hashCode + (this.getKeyManager() == null ? 0 : this.getKeyManager().hashCode());
        hashCode = 31 * hashCode + (this.getCustomerMasterKeySpec() == null ? 0 : this.getCustomerMasterKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getKeySpec() == null ? 0 : this.getKeySpec().hashCode());
        hashCode = 31 * hashCode + (this.getEncryptionAlgorithms() == null ? 0 : this.getEncryptionAlgorithms().hashCode());
        hashCode = 31 * hashCode + (this.getSigningAlgorithms() == null ? 0 : this.getSigningAlgorithms().hashCode());
        hashCode = 31 * hashCode + (this.getMultiRegion() == null ? 0 : this.getMultiRegion().hashCode());
        hashCode = 31 * hashCode + (this.getMultiRegionConfiguration() == null ? 0 : this.getMultiRegionConfiguration().hashCode());
        hashCode = 31 * hashCode + (this.getPendingDeletionWindowInDays() == null ? 0 : this.getPendingDeletionWindowInDays().hashCode());
        hashCode = 31 * hashCode + (this.getMacAlgorithms() == null ? 0 : this.getMacAlgorithms().hashCode());
        return hashCode;
    }

    public KeyMetadata clone() {
        try {
            return (KeyMetadata)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        KeyMetadataMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

