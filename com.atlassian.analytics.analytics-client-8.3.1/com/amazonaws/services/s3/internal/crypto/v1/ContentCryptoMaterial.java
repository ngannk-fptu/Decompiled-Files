/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.v1;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.s3.KeyWrapException;
import com.amazonaws.services.s3.internal.crypto.CipherLite;
import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;
import com.amazonaws.services.s3.internal.crypto.CryptoUtils;
import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KMSKeyWrapperContext;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapper;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperContext;
import com.amazonaws.services.s3.internal.crypto.keywrap.KeyWrapperFactory;
import com.amazonaws.services.s3.internal.crypto.v1.KMSMaterialsHandler;
import com.amazonaws.services.s3.internal.crypto.v1.KMSSecuredCEK;
import com.amazonaws.services.s3.internal.crypto.v1.S3CryptoScheme;
import com.amazonaws.services.s3.internal.crypto.v1.S3KeyWrapScheme;
import com.amazonaws.services.s3.internal.crypto.v1.SecuredCEK;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.EncryptionMaterialsAccessor;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.ExtraMaterialsDescription;
import com.amazonaws.services.s3.model.KMSEncryptionMaterials;
import com.amazonaws.services.s3.model.MaterialsDescriptionProvider;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.Base64;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.Throwables;
import com.amazonaws.util.json.Jackson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

final class ContentCryptoMaterial {
    private final String keyWrappingAlgorithm;
    private final CipherLite cipherLite;
    private final Map<String, String> kekMaterialsDescription;
    private final byte[] encryptedCEK;

    ContentCryptoMaterial(Map<String, String> kekMaterialsDescription, byte[] encryptedCEK, String keyWrappingAlgorithm, CipherLite cipherLite) {
        this.cipherLite = cipherLite;
        this.keyWrappingAlgorithm = keyWrappingAlgorithm;
        this.encryptedCEK = (byte[])encryptedCEK.clone();
        this.kekMaterialsDescription = kekMaterialsDescription;
    }

    String getKeyWrappingAlgorithm() {
        return this.keyWrappingAlgorithm;
    }

    private boolean usesKMSKey() {
        return KMSSecuredCEK.isKMSKeyWrapped(this.keyWrappingAlgorithm);
    }

    ContentCryptoScheme getContentCryptoScheme() {
        return this.cipherLite.getContentCryptoScheme();
    }

    ObjectMetadata toObjectMetadata(ObjectMetadata metadata, CryptoMode mode) {
        return mode == CryptoMode.EncryptionOnly && !this.usesKMSKey() ? this.toObjectMetadataEO(metadata) : this.toObjectMetadata(metadata);
    }

    private ObjectMetadata toObjectMetadata(ObjectMetadata metadata) {
        String keyWrapAlgo;
        byte[] encryptedCEK = this.getEncryptedCEK();
        metadata.addUserMetadata("x-amz-key-v2", Base64.encodeAsString(encryptedCEK));
        byte[] iv = this.cipherLite.getIV();
        metadata.addUserMetadata("x-amz-iv", Base64.encodeAsString(iv));
        metadata.addUserMetadata("x-amz-matdesc", this.kekMaterialDescAsJson());
        ContentCryptoScheme scheme = this.getContentCryptoScheme();
        metadata.addUserMetadata("x-amz-cek-alg", scheme.getCipherAlgorithm());
        int tagLen = scheme.getTagLengthInBits();
        if (tagLen > 0) {
            metadata.addUserMetadata("x-amz-tag-len", String.valueOf(tagLen));
        }
        if ((keyWrapAlgo = this.getKeyWrappingAlgorithm()) != null) {
            metadata.addUserMetadata("x-amz-wrap-alg", keyWrapAlgo);
        }
        return metadata;
    }

    private ObjectMetadata toObjectMetadataEO(ObjectMetadata metadata) {
        byte[] encryptedCEK = this.getEncryptedCEK();
        metadata.addUserMetadata("x-amz-key", Base64.encodeAsString(encryptedCEK));
        byte[] iv = this.cipherLite.getIV();
        metadata.addUserMetadata("x-amz-iv", Base64.encodeAsString(iv));
        metadata.addUserMetadata("x-amz-matdesc", this.kekMaterialDescAsJson());
        return metadata;
    }

    String toJsonString(CryptoMode mode) {
        return mode == CryptoMode.EncryptionOnly && !this.usesKMSKey() ? this.toJsonStringEO() : this.toJsonString();
    }

    private String toJsonString() {
        String keyWrapAlgo;
        HashMap<String, String> map = new HashMap<String, String>();
        byte[] encryptedCEK = this.getEncryptedCEK();
        map.put("x-amz-key-v2", Base64.encodeAsString(encryptedCEK));
        byte[] iv = this.cipherLite.getIV();
        map.put("x-amz-iv", Base64.encodeAsString(iv));
        map.put("x-amz-matdesc", this.kekMaterialDescAsJson());
        ContentCryptoScheme scheme = this.getContentCryptoScheme();
        map.put("x-amz-cek-alg", scheme.getCipherAlgorithm());
        int tagLen = scheme.getTagLengthInBits();
        if (tagLen > 0) {
            map.put("x-amz-tag-len", String.valueOf(tagLen));
        }
        if ((keyWrapAlgo = this.getKeyWrappingAlgorithm()) != null) {
            map.put("x-amz-wrap-alg", keyWrapAlgo);
        }
        return Jackson.toJsonString(map);
    }

    private String toJsonStringEO() {
        HashMap<String, String> map = new HashMap<String, String>();
        byte[] encryptedCEK = this.getEncryptedCEK();
        map.put("x-amz-key", Base64.encodeAsString(encryptedCEK));
        byte[] iv = this.cipherLite.getIV();
        map.put("x-amz-iv", Base64.encodeAsString(iv));
        map.put("x-amz-matdesc", this.kekMaterialDescAsJson());
        return Jackson.toJsonString(map);
    }

    private String kekMaterialDescAsJson() {
        Map<String, String> kekMaterialDesc = this.getKEKMaterialsDescription();
        if (kekMaterialDesc == null) {
            kekMaterialDesc = Collections.emptyMap();
        }
        return Jackson.toJsonString(kekMaterialDesc);
    }

    private static Map<String, String> matdescFromJson(String json) {
        Map<String, String> map = Jackson.stringMapFromJsonString(json);
        return map == null ? null : Collections.unmodifiableMap(map);
    }

    private static SecretKey cek(byte[] cekSecured, String keyWrapAlgo, EncryptionMaterials materials, Provider securityProvider, ContentCryptoScheme contentCryptoScheme, AWSKMS kms) {
        Key kek;
        InternalKeyWrapAlgorithm internalKeyWrapAlgorithm = InternalKeyWrapAlgorithm.fromAlgorithmName(keyWrapAlgo);
        if (internalKeyWrapAlgorithm != null && !internalKeyWrapAlgorithm.isV1Algorithm()) {
            KMSKeyWrapperContext kmsContext = null;
            if (internalKeyWrapAlgorithm.isKMS()) {
                Map<String, String> kmsMaterialsDescription = KMSMaterialsHandler.createKMSContextMaterialsDescription(materials.getMaterialsDescription(), contentCryptoScheme.getCipherAlgorithm());
                kmsContext = KMSKeyWrapperContext.builder().kms(kms).kmsMaterialsDescription(kmsMaterialsDescription).build();
            }
            KeyWrapperContext context = KeyWrapperContext.builder().cryptoProvider(securityProvider).internalKeyWrapAlgorithm(internalKeyWrapAlgorithm).materials(materials).cekSecured(cekSecured).contentCryptoScheme(contentCryptoScheme).kmsKeyWrapperContext(kmsContext).build();
            return ContentCryptoMaterial.cekV2(context);
        }
        if (KMSSecuredCEK.isKMSKeyWrapped(keyWrapAlgo)) {
            return ContentCryptoMaterial.cekByKMS(cekSecured, keyWrapAlgo, materials, contentCryptoScheme, kms);
        }
        if (materials.getKeyPair() != null ? (kek = materials.getKeyPair().getPrivate()) == null : (kek = materials.getSymmetricKey()) == null) {
            throw new SdkClientException("Key encrypting key not available");
        }
        try {
            if (keyWrapAlgo != null) {
                Cipher cipher = securityProvider == null ? Cipher.getInstance(keyWrapAlgo) : Cipher.getInstance(keyWrapAlgo, securityProvider);
                cipher.init(4, kek);
                return (SecretKey)cipher.unwrap(cekSecured, keyWrapAlgo, 3);
            }
            Cipher cipher = securityProvider != null ? Cipher.getInstance(kek.getAlgorithm(), securityProvider) : Cipher.getInstance(kek.getAlgorithm());
            cipher.init(2, kek);
            byte[] decryptedSymmetricKeyBytes = cipher.doFinal(cekSecured);
            return new SecretKeySpec(decryptedSymmetricKeyBytes, "AES");
        }
        catch (Exception e) {
            throw Throwables.failure(e, "Unable to decrypt symmetric key from object metadata");
        }
    }

    private static SecretKey cekV2(KeyWrapperContext context) {
        if (context.internalKeyWrapAlgorithm().isKMS()) {
            ContentCryptoMaterial.validateKMSParameters(context);
        }
        Key kek = ContentCryptoMaterial.getDecryptionKeyFrom(context.materials());
        String keyGeneratorAlgorithm = context.internalKeyWrapAlgorithm().isKMS() ? context.contentCryptoScheme().getKeyGeneratorAlgorithm() : kek.getAlgorithm();
        KeyWrapper keyWrapper = KeyWrapperFactory.defaultInstance().createKeyWrapper(context);
        return new SecretKeySpec(keyWrapper.unwrapCek(context.cekSecured(), kek), keyGeneratorAlgorithm);
    }

    private static void validateKMSParameters(KeyWrapperContext context) {
        KMSKeyWrapperContext kmsKeyWrapperContext = context.kmsKeyWrapperContext();
        if (kmsKeyWrapperContext == null) {
            throw new IllegalStateException("Missing KMS parameters");
        }
        Map<String, String> kmsMaterialsDescription = kmsKeyWrapperContext.kmsMaterialsDescription();
        if (kmsMaterialsDescription == null) {
            throw new IllegalStateException("Key materials from KMS must contain description entries");
        }
        String cekAlgoFromMaterials = kmsMaterialsDescription.get("aws:x-amz-cek-alg");
        if (cekAlgoFromMaterials == null) {
            throw new IllegalStateException("Could not find required description in key material: aws:x-amz-cek-alg");
        }
        String cekAlgoFromCryptoScheme = CryptoUtils.normalizeContentAlgorithmForValidation(context.contentCryptoScheme().getCipherAlgorithm());
        if (!cekAlgoFromMaterials.equals(cekAlgoFromCryptoScheme)) {
            throw new IllegalStateException("Algorithm values from materials and metadata/instruction file don't match:" + cekAlgoFromMaterials + ", " + cekAlgoFromCryptoScheme);
        }
    }

    private static Key getDecryptionKeyFrom(EncryptionMaterials materials) {
        if (materials.isKMSEnabled()) {
            return null;
        }
        return materials.getKeyPair() != null ? materials.getKeyPair().getPrivate() : materials.getSymmetricKey();
    }

    private static SecretKey cekByKMS(byte[] cekSecured, String keyWrapAlgo, EncryptionMaterials materials, ContentCryptoScheme contentCryptoScheme, AWSKMS kms) {
        DecryptRequest kmsreq = new DecryptRequest().withEncryptionContext(materials.getMaterialsDescription()).withCiphertextBlob(ByteBuffer.wrap(cekSecured));
        DecryptResult result = kms.decrypt(kmsreq);
        return new SecretKeySpec(BinaryUtils.copyAllBytesFrom(result.getPlaintext()), contentCryptoScheme.getKeyGeneratorAlgorithm());
    }

    static ContentCryptoMaterial fromObjectMetadata(ObjectMetadata metadata, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, boolean keyWrapExpected, AWSKMS kms) {
        return ContentCryptoMaterial.fromObjectMetadata0(metadata, kekMaterialAccessor, securityProvider, alwaysUseSecurityProvider, null, ExtraMaterialsDescription.NONE, keyWrapExpected, kms);
    }

    static ContentCryptoMaterial fromObjectMetadata(ObjectMetadata metadata, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, long[] range, ExtraMaterialsDescription extra, boolean keyWrapExpected, AWSKMS kms) {
        return ContentCryptoMaterial.fromObjectMetadata0(metadata, kekMaterialAccessor, securityProvider, alwaysUseSecurityProvider, range, extra, keyWrapExpected, kms);
    }

    private static ContentCryptoMaterial fromObjectMetadata0(ObjectMetadata metadata, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, long[] range, ExtraMaterialsDescription extra, boolean keyWrapExpected, AWSKMS kms) {
        Map<String, String> userMeta = metadata.getUserMetadata();
        String b64key = userMeta.get("x-amz-key-v2");
        if (b64key == null && (b64key = userMeta.get("x-amz-key")) == null) {
            throw new SdkClientException("Content encrypting key not found.");
        }
        byte[] cekWrapped = Base64.decode(b64key);
        byte[] iv = Base64.decode(userMeta.get("x-amz-iv"));
        if (cekWrapped == null || iv == null) {
            throw new SdkClientException("Content encrypting key or IV not found.");
        }
        String matdescStr = userMeta.get("x-amz-matdesc");
        String keyWrapAlgo = userMeta.get("x-amz-wrap-alg");
        Map<String, String> coreMatDesc = ContentCryptoMaterial.matdescFromJson(matdescStr);
        boolean isKMSV1 = KMSSecuredCEK.isKMSV1KeyWrapped(keyWrapAlgo);
        boolean isKMSV2 = KMSSecuredCEK.isKMSV2KeyWrapped(keyWrapAlgo);
        Map<String, String> mergedMatDesc = isKMSV1 || isKMSV2 || extra == null ? coreMatDesc : extra.mergeInto(coreMatDesc);
        EncryptionMaterials materials = null;
        if (isKMSV1) {
            if (materials == null) {
                materials = new KMSEncryptionMaterials(coreMatDesc.get("kms_cmk_id"));
                materials.addDescriptions(coreMatDesc);
            }
        } else if (isKMSV2) {
            EncryptionMaterials encryptionMaterials = materials = kekMaterialAccessor instanceof EncryptionMaterialsProvider ? ((EncryptionMaterialsProvider)kekMaterialAccessor).getEncryptionMaterials() : null;
            if (!(materials instanceof KMSEncryptionMaterials)) {
                throw new SdkClientException("Retrieved materials not of expected type KMSEncryptionMaterials");
            }
        } else {
            materials = kekMaterialAccessor.getEncryptionMaterials(mergedMatDesc);
        }
        if (materials == null) {
            throw new SdkClientException("Unable to retrieve the client encryption materials");
        }
        String cekAlgo = userMeta.get("x-amz-cek-alg");
        boolean isRangeGet = range != null;
        ContentCryptoScheme contentCryptoScheme = ContentCryptoScheme.fromCEKAlgo(cekAlgo, isRangeGet);
        if (isRangeGet) {
            iv = contentCryptoScheme.adjustIV(iv, range[0]);
        } else {
            String s;
            int tagLenActual;
            int tagLenExpected = contentCryptoScheme.getTagLengthInBits();
            if (tagLenExpected > 0 && tagLenExpected != (tagLenActual = Integer.parseInt(s = userMeta.get("x-amz-tag-len")))) {
                throw new SdkClientException("Unsupported tag length: " + tagLenActual + ", expected: " + tagLenExpected);
            }
        }
        if (keyWrapExpected && keyWrapAlgo == null) {
            throw ContentCryptoMaterial.newKeyWrapException();
        }
        SecretKey cek = ContentCryptoMaterial.cek(cekWrapped, keyWrapAlgo, materials, securityProvider, contentCryptoScheme, kms);
        return new ContentCryptoMaterial(mergedMatDesc, cekWrapped, keyWrapAlgo, contentCryptoScheme.createCipherLite(cek, iv, 2, securityProvider, alwaysUseSecurityProvider));
    }

    private static KeyWrapException newKeyWrapException() {
        return new KeyWrapException("Missing key-wrap for the content-encrypting-key");
    }

    static ContentCryptoMaterial fromInstructionFile(Map<String, String> instFile, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, boolean keyWrapExpected, AWSKMS kms) {
        return ContentCryptoMaterial.fromInstructionFile0(instFile, kekMaterialAccessor, securityProvider, alwaysUseSecurityProvider, null, ExtraMaterialsDescription.NONE, keyWrapExpected, kms);
    }

    static ContentCryptoMaterial fromInstructionFile(Map<String, String> instFile, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, long[] range, ExtraMaterialsDescription extra, boolean keyWrapExpected, AWSKMS kms) {
        return ContentCryptoMaterial.fromInstructionFile0(instFile, kekMaterialAccessor, securityProvider, alwaysUseSecurityProvider, range, extra, keyWrapExpected, kms);
    }

    private static ContentCryptoMaterial fromInstructionFile0(Map<String, String> instFile, EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider, boolean alwaysUseSecurityProvider, long[] range, ExtraMaterialsDescription extra, boolean keyWrapExpected, AWSKMS kms) {
        String b64key = instFile.get("x-amz-key-v2");
        if (b64key == null && (b64key = instFile.get("x-amz-key")) == null) {
            throw new SdkClientException("Content encrypting key not found.");
        }
        byte[] cekWrapped = Base64.decode(b64key);
        byte[] iv = Base64.decode(instFile.get("x-amz-iv"));
        if (cekWrapped == null || iv == null) {
            throw new SdkClientException("Necessary encryption info not found in the instruction file " + instFile);
        }
        String matdescStr = instFile.get("x-amz-matdesc");
        String keyWrapAlgo = instFile.get("x-amz-wrap-alg");
        Map<String, String> coreMatDesc = ContentCryptoMaterial.matdescFromJson(matdescStr);
        boolean isKMSV1 = KMSSecuredCEK.isKMSV1KeyWrapped(keyWrapAlgo);
        boolean isKMSV2 = KMSSecuredCEK.isKMSV2KeyWrapped(keyWrapAlgo);
        Map<String, String> mergedMatDesc = isKMSV1 || isKMSV2 || extra == null ? coreMatDesc : extra.mergeInto(coreMatDesc);
        EncryptionMaterials materials = null;
        if (isKMSV1) {
            if (materials == null) {
                materials = new KMSEncryptionMaterials(coreMatDesc.get("kms_cmk_id"));
                materials.addDescriptions(coreMatDesc);
            }
        } else if (isKMSV2) {
            EncryptionMaterials encryptionMaterials = materials = kekMaterialAccessor instanceof EncryptionMaterialsProvider ? ((EncryptionMaterialsProvider)kekMaterialAccessor).getEncryptionMaterials() : null;
            if (!(materials instanceof KMSEncryptionMaterials)) {
                throw new SdkClientException("Retrieved materials not of expected type KMSEncryptionMaterials");
            }
        } else {
            materials = kekMaterialAccessor.getEncryptionMaterials(mergedMatDesc);
        }
        if (materials == null) {
            throw new SdkClientException("Unable to retrieve the encryption materials that originally encrypted object corresponding to instruction file " + instFile);
        }
        String cekAlgo = instFile.get("x-amz-cek-alg");
        boolean isRangeGet = range != null;
        ContentCryptoScheme contentCryptoScheme = ContentCryptoScheme.fromCEKAlgo(cekAlgo, isRangeGet);
        if (isRangeGet) {
            iv = contentCryptoScheme.adjustIV(iv, range[0]);
        } else {
            String s;
            int tagLenActual;
            int tagLenExpected = contentCryptoScheme.getTagLengthInBits();
            if (tagLenExpected > 0 && tagLenExpected != (tagLenActual = Integer.parseInt(s = instFile.get("x-amz-tag-len")))) {
                throw new SdkClientException("Unsupported tag length: " + tagLenActual + ", expected: " + tagLenExpected);
            }
        }
        if (keyWrapExpected && keyWrapAlgo == null) {
            throw ContentCryptoMaterial.newKeyWrapException();
        }
        SecretKey cek = ContentCryptoMaterial.cek(cekWrapped, keyWrapAlgo, materials, securityProvider, contentCryptoScheme, kms);
        return new ContentCryptoMaterial(mergedMatDesc, cekWrapped, keyWrapAlgo, contentCryptoScheme.createCipherLite(cek, iv, 2, securityProvider, alwaysUseSecurityProvider));
    }

    static String parseInstructionFile(S3Object instructionFile) {
        try {
            return ContentCryptoMaterial.convertStreamToString(instructionFile.getObjectContent());
        }
        catch (Exception e) {
            throw Throwables.failure(e, "Error parsing JSON instruction file");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StringUtils.UTF8));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        finally {
            inputStream.close();
        }
        return stringBuilder.toString();
    }

    CipherLite getCipherLite() {
        return this.cipherLite;
    }

    Map<String, String> getKEKMaterialsDescription() {
        return this.kekMaterialsDescription;
    }

    byte[] getEncryptedCEK() {
        return (byte[])this.encryptedCEK.clone();
    }

    ContentCryptoMaterial recreate(Map<String, String> newKEKMatDesc, EncryptionMaterialsAccessor accessor, S3CryptoScheme targetScheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        if (!this.usesKMSKey() && newKEKMatDesc.equals(this.kekMaterialsDescription)) {
            throw new SecurityException("Material description of the new KEK must differ from the current one");
        }
        EncryptionMaterials origKEK = this.usesKMSKey() ? new KMSEncryptionMaterials(this.kekMaterialsDescription.get("kms_cmk_id")) : accessor.getEncryptionMaterials(this.kekMaterialsDescription);
        EncryptionMaterials newKEK = accessor.getEncryptionMaterials(newKEKMatDesc);
        if (newKEK == null) {
            throw new SdkClientException("No material available with the description " + newKEKMatDesc + " from the encryption material provider");
        }
        SecretKey cek = ContentCryptoMaterial.cek(this.encryptedCEK, this.keyWrappingAlgorithm, origKEK, config.getCryptoProvider(), this.getContentCryptoScheme(), kms);
        ContentCryptoMaterial output = ContentCryptoMaterial.create(cek, this.cipherLite.getIV(), newKEK, this.getContentCryptoScheme(), targetScheme, config, kms, req);
        if (Arrays.equals(output.encryptedCEK, this.encryptedCEK)) {
            throw new SecurityException("The new KEK must differ from the original");
        }
        return output;
    }

    ContentCryptoMaterial recreate(EncryptionMaterials newKEK, EncryptionMaterialsAccessor accessor, S3CryptoScheme targetScheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        if (!this.usesKMSKey() && newKEK.getMaterialsDescription().equals(this.kekMaterialsDescription)) {
            throw new SecurityException("Material description of the new KEK must differ from the current one");
        }
        EncryptionMaterials origKEK = this.usesKMSKey() ? new KMSEncryptionMaterials(this.kekMaterialsDescription.get("kms_cmk_id")) : accessor.getEncryptionMaterials(this.kekMaterialsDescription);
        SecretKey cek = ContentCryptoMaterial.cek(this.encryptedCEK, this.keyWrappingAlgorithm, origKEK, config.getCryptoProvider(), this.getContentCryptoScheme(), kms);
        ContentCryptoMaterial output = ContentCryptoMaterial.create(cek, this.cipherLite.getIV(), newKEK, this.getContentCryptoScheme(), targetScheme, config, kms, req);
        if (Arrays.equals(output.encryptedCEK, this.encryptedCEK)) {
            throw new SecurityException("The new KEK must differ from the original");
        }
        return output;
    }

    static ContentCryptoMaterial create(SecretKey cek, byte[] iv, EncryptionMaterials kekMaterials, ContentCryptoScheme contentCryptoScheme, S3CryptoScheme targetScheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        return ContentCryptoMaterial.doCreate(cek, iv, kekMaterials, contentCryptoScheme, targetScheme, config, kms, req);
    }

    static ContentCryptoMaterial create(SecretKey cek, byte[] iv, EncryptionMaterials kekMaterials, S3CryptoScheme scheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        return ContentCryptoMaterial.doCreate(cek, iv, kekMaterials, scheme.getContentCryptoScheme(), scheme, config, kms, req);
    }

    private static ContentCryptoMaterial doCreate(SecretKey cek, byte[] iv, EncryptionMaterials kekMaterials, ContentCryptoScheme contentCryptoScheme, S3CryptoScheme targetS3CryptoScheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        SecuredCEK cekSecured = ContentCryptoMaterial.secureCEK(cek, kekMaterials, targetS3CryptoScheme.getKeyWrapScheme(), config, kms, req);
        return ContentCryptoMaterial.wrap(cek, iv, contentCryptoScheme, config.getCryptoProvider(), config.getAlwaysUseCryptoProvider(), cekSecured);
    }

    public static ContentCryptoMaterial wrap(SecretKey cek, byte[] iv, ContentCryptoScheme contentCryptoScheme, Provider provider, boolean alwaysUseProvider, SecuredCEK cekSecured) {
        return new ContentCryptoMaterial(cekSecured.getMaterialDescription(), cekSecured.getEncrypted(), cekSecured.getKeyWrapAlgorithm(), contentCryptoScheme.createCipherLite(cek, iv, 1, provider, alwaysUseProvider));
    }

    private static SecuredCEK secureCEK(SecretKey cek, EncryptionMaterials materials, S3KeyWrapScheme kwScheme, CryptoConfiguration config, AWSKMS kms, AmazonWebServiceRequest req) {
        if (materials.isKMSEnabled()) {
            Map<String, String> matdesc = ContentCryptoMaterial.mergeMaterialDescriptions(materials, req);
            EncryptRequest encryptRequest = new EncryptRequest().withEncryptionContext(matdesc).withKeyId(materials.getCustomerMasterKeyId()).withPlaintext(ByteBuffer.wrap(cek.getEncoded()));
            ((AmazonWebServiceRequest)encryptRequest.withGeneralProgressListener(req.getGeneralProgressListener())).withRequestMetricCollector(req.getRequestMetricCollector());
            EncryptResult encryptResult = kms.encrypt(encryptRequest);
            byte[] keyBlob = BinaryUtils.copyAllBytesFrom(encryptResult.getCiphertextBlob());
            return new KMSSecuredCEK(keyBlob, matdesc);
        }
        Map<String, String> matdesc = materials.getMaterialsDescription();
        Key kek = materials.getKeyPair() != null ? materials.getKeyPair().getPublic() : materials.getSymmetricKey();
        String keyWrapAlgo = kwScheme.getKeyWrapAlgorithm(kek);
        Provider provider = config.getCryptoProvider();
        SecureRandom srand = config.getSecureRandom();
        try {
            if (keyWrapAlgo != null) {
                Cipher cipher = provider == null ? Cipher.getInstance(keyWrapAlgo) : Cipher.getInstance(keyWrapAlgo, provider);
                cipher.init(3, kek, srand);
                return new SecuredCEK(cipher.wrap(cek), keyWrapAlgo, matdesc);
            }
            byte[] toBeEncryptedBytes = cek.getEncoded();
            String algo = kek.getAlgorithm();
            Cipher cipher = provider != null ? Cipher.getInstance(algo, provider) : Cipher.getInstance(algo);
            cipher.init(1, kek);
            return new SecuredCEK(cipher.doFinal(toBeEncryptedBytes), null, matdesc);
        }
        catch (Exception e) {
            throw Throwables.failure(e, "Unable to encrypt symmetric key");
        }
    }

    static Map<String, String> mergeMaterialDescriptions(EncryptionMaterials materials, AmazonWebServiceRequest req) {
        MaterialsDescriptionProvider mdp;
        Map<String, String> matdesc_req;
        Map<String, String> matdesc = materials.getMaterialsDescription();
        if (req instanceof MaterialsDescriptionProvider && (matdesc_req = (mdp = (MaterialsDescriptionProvider)((Object)req)).getMaterialsDescription()) != null) {
            matdesc = new TreeMap<String, String>(matdesc);
            matdesc.putAll(matdesc_req);
        }
        return matdesc;
    }
}

