/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.v2;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.SdkFilterInputStream;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.s3.internal.S3Direct;
import com.amazonaws.services.s3.internal.crypto.AdjustedRangeInputStream;
import com.amazonaws.services.s3.internal.crypto.CipherLite;
import com.amazonaws.services.s3.internal.crypto.CipherLiteInputStream;
import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;
import com.amazonaws.services.s3.internal.crypto.CryptoRuntime;
import com.amazonaws.services.s3.internal.crypto.v2.ContentCryptoMaterial;
import com.amazonaws.services.s3.internal.crypto.v2.MultipartUploadCryptoContext;
import com.amazonaws.services.s3.internal.crypto.v2.S3CryptoModuleBase;
import com.amazonaws.services.s3.internal.crypto.v2.S3ObjectWrapper;
import com.amazonaws.services.s3.model.CryptoConfigurationV2;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.CryptoRangeGetMode;
import com.amazonaws.services.s3.model.EncryptedGetObjectRequest;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.ExtraMaterialsDescription;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.json.Jackson;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public class S3CryptoModuleAE
extends S3CryptoModuleBase<MultipartUploadCryptoContext> {
    public S3CryptoModuleAE(AWSKMS kms, S3Direct s3, AWSCredentialsProvider credentialsProvider, EncryptionMaterialsProvider encryptionMaterialsProvider, CryptoConfigurationV2 cryptoConfig) {
        super(kms, s3, encryptionMaterialsProvider, cryptoConfig);
        CryptoMode mode = cryptoConfig.getCryptoMode();
        if (mode != CryptoMode.StrictAuthenticatedEncryption && mode != CryptoMode.AuthenticatedEncryption) {
            throw new IllegalArgumentException();
        }
    }

    S3CryptoModuleAE(S3Direct s3, EncryptionMaterialsProvider encryptionMaterialsProvider, CryptoConfigurationV2 cryptoConfig) {
        this(null, s3, new DefaultAWSCredentialsProviderChain(), encryptionMaterialsProvider, cryptoConfig);
    }

    S3CryptoModuleAE(AWSKMS kms, S3Direct s3, EncryptionMaterialsProvider encryptionMaterialsProvider, CryptoConfigurationV2 cryptoConfig) {
        this(kms, s3, new DefaultAWSCredentialsProviderChain(), encryptionMaterialsProvider, cryptoConfig);
    }

    protected boolean isStrict() {
        return false;
    }

    @Override
    public S3Object getObjectSecurely(GetObjectRequest req) {
        S3Object retrieved;
        long[] adjustedCryptoRange;
        boolean isPartialObject;
        long[] desiredRange = req.getRange();
        boolean bl = isPartialObject = desiredRange != null || req.getPartNumber() != null;
        if (isPartialObject) {
            this.assertCanGetPartialObject();
        }
        if ((adjustedCryptoRange = S3CryptoModuleAE.getAdjustedCryptoRange(desiredRange)) != null) {
            req.setRange(adjustedCryptoRange[0], adjustedCryptoRange[1]);
        }
        if ((retrieved = this.s3.getObject(req)) == null) {
            return null;
        }
        String suffix = null;
        if (req instanceof EncryptedGetObjectRequest) {
            EncryptedGetObjectRequest ereq = (EncryptedGetObjectRequest)req;
            suffix = ereq.getInstructionFileSuffix();
        }
        try {
            return suffix == null || suffix.trim().isEmpty() ? this.decipher(req, desiredRange, adjustedCryptoRange, retrieved) : this.decipherWithInstFileSuffix(req, desiredRange, adjustedCryptoRange, retrieved, suffix);
        }
        catch (RuntimeException ex) {
            IOUtils.closeQuietly(retrieved, this.log);
            throw ex;
        }
        catch (Error error) {
            IOUtils.closeQuietly(retrieved, this.log);
            throw error;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private S3Object decipher(GetObjectRequest req, long[] desiredRange, long[] cryptoRange, S3Object retrieved) {
        S3ObjectWrapper wrapped = new S3ObjectWrapper(retrieved, req.getS3ObjectId());
        if (wrapped.hasEncryptionInfo()) {
            return this.decipherWithMetadata(req, desiredRange, cryptoRange, wrapped);
        }
        S3ObjectWrapper ifile = this.fetchInstructionFile(req.getS3ObjectId(), null);
        if (ifile != null) {
            try {
                S3Object s3Object = this.decipherWithInstructionFile(req, desiredRange, cryptoRange, wrapped, ifile);
                return s3Object;
            }
            finally {
                IOUtils.closeQuietly(ifile, this.log);
            }
        }
        if (this.isStrict()) {
            IOUtils.closeQuietly(wrapped, this.log);
            throw new SecurityException("Unencrypted object found, cannot be decrypted in mode " + (Object)((Object)CryptoMode.StrictAuthenticatedEncryption) + "; bucket name: " + retrieved.getBucketName() + ", key: " + retrieved.getKey());
        }
        if (this.cryptoConfig.isUnsafeUndecryptableObjectPassthrough()) {
            this.log.warn((Object)String.format("Unable to detect encryption information for object '%s' in bucket '%s'. Returning object without decryption.", retrieved.getKey(), retrieved.getBucketName()));
            S3ObjectWrapper adjusted = this.adjustToDesiredRange(wrapped, desiredRange, null);
            return adjusted.getS3Object();
        }
        IOUtils.closeQuietly(wrapped, this.log);
        throw new SecurityException("Instruction file not found for S3 object with bucket name: " + retrieved.getBucketName() + ", key: " + retrieved.getKey());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private S3Object decipherWithInstFileSuffix(GetObjectRequest req, long[] desiredRange, long[] cryptoRange, S3Object retrieved, String instFileSuffix) {
        S3ObjectId id = req.getS3ObjectId();
        S3ObjectWrapper ifile = this.fetchInstructionFile(id, instFileSuffix);
        if (ifile == null) {
            throw new SdkClientException("Instruction file with suffix " + instFileSuffix + " is not found for " + retrieved);
        }
        try {
            S3Object s3Object = this.decipherWithInstructionFile(req, desiredRange, cryptoRange, new S3ObjectWrapper(retrieved, id), ifile);
            return s3Object;
        }
        finally {
            IOUtils.closeQuietly(ifile, this.log);
        }
    }

    private S3Object decipherWithInstructionFile(GetObjectRequest req, long[] desiredRange, long[] cryptoRange, S3ObjectWrapper retrieved, S3ObjectWrapper instructionFile) {
        ExtraMaterialsDescription extraMatDesc = ExtraMaterialsDescription.NONE;
        boolean keyWrapExpected = this.isStrict();
        if (req instanceof EncryptedGetObjectRequest) {
            EncryptedGetObjectRequest ereq = (EncryptedGetObjectRequest)req;
            extraMatDesc = ereq.getExtraMaterialDescription();
            if (!keyWrapExpected) {
                keyWrapExpected = ereq.isKeyWrapExpected();
            }
        }
        String json = instructionFile.toJsonString();
        Map<String, String> matdesc = Collections.unmodifiableMap(Jackson.stringMapFromJsonString(json));
        ContentCryptoMaterial cekMaterial = ContentCryptoMaterial.fromInstructionFile(matdesc, this.kekMaterialsProvider, this.cryptoConfig, cryptoRange, extraMatDesc, keyWrapExpected, this.kms);
        boolean isRangeGet = desiredRange != null;
        this.securityCheck(cekMaterial, retrieved.getS3ObjectId(), isRangeGet);
        S3ObjectWrapper decrypted = this.decrypt(retrieved, cekMaterial, cryptoRange);
        S3ObjectWrapper adjusted = this.adjustToDesiredRange(decrypted, desiredRange, matdesc);
        return adjusted.getS3Object();
    }

    private S3Object decipherWithMetadata(GetObjectRequest req, long[] desiredRange, long[] cryptoRange, S3ObjectWrapper retrieved) {
        ExtraMaterialsDescription extraMatDesc = ExtraMaterialsDescription.NONE;
        boolean keyWrapExpected = this.isStrict();
        if (req instanceof EncryptedGetObjectRequest) {
            EncryptedGetObjectRequest ereq = (EncryptedGetObjectRequest)req;
            extraMatDesc = ereq.getExtraMaterialDescription();
            if (!keyWrapExpected) {
                keyWrapExpected = ereq.isKeyWrapExpected();
            }
        }
        ContentCryptoMaterial cekMaterial = ContentCryptoMaterial.fromObjectMetadata(retrieved.getObjectMetadata().getUserMetadata(), this.kekMaterialsProvider, this.cryptoConfig, cryptoRange, extraMatDesc, keyWrapExpected, this.kms);
        boolean isRangeGet = desiredRange != null;
        this.securityCheck(cekMaterial, retrieved.getS3ObjectId(), isRangeGet);
        S3ObjectWrapper decrypted = this.decrypt(retrieved, cekMaterial, cryptoRange);
        S3ObjectWrapper adjusted = this.adjustToDesiredRange(decrypted, desiredRange, null);
        return adjusted.getS3Object();
    }

    protected final S3ObjectWrapper adjustToDesiredRange(S3ObjectWrapper s3object, long[] range, Map<String, String> instruction) {
        if (range == null) {
            return s3object;
        }
        ContentCryptoScheme encryptionScheme = s3object.encryptionSchemeOf(instruction);
        long instanceLen = s3object.getObjectMetadata().getInstanceLength();
        long maxOffset = instanceLen - (long)(encryptionScheme.getTagLengthInBits() / 8) - 1L;
        if (range[1] > maxOffset) {
            range[1] = maxOffset;
            if (range[0] > range[1]) {
                IOUtils.closeQuietly(s3object.getObjectContent(), this.log);
                s3object.setObjectContent(new ByteArrayInputStream(new byte[0]));
                return s3object;
            }
        }
        if (range[0] > range[1]) {
            return s3object;
        }
        try {
            S3ObjectInputStream objectContent = s3object.getObjectContent();
            AdjustedRangeInputStream adjustedRangeContents = new AdjustedRangeInputStream(objectContent, range[0], range[1]);
            s3object.setObjectContent(new S3ObjectInputStream(adjustedRangeContents, objectContent.getHttpRequest()));
            return s3object;
        }
        catch (IOException e) {
            throw new SdkClientException("Error adjusting output to desired byte range: " + e.getMessage());
        }
    }

    @Override
    public ObjectMetadata getObjectSecurely(GetObjectRequest getObjectRequest, File destinationFile) {
        this.assertParameterNotNull(destinationFile, "The destination file parameter must be specified when downloading an object directly to a file");
        S3Object s3Object = this.getObjectSecurely(getObjectRequest);
        if (s3Object == null) {
            return null;
        }
        BufferedOutputStream outputStream = null;
        try {
            int bytesRead;
            outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buffer = new byte[10240];
            while ((bytesRead = s3Object.getObjectContent().read(buffer)) > -1) {
                ((OutputStream)outputStream).write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            try {
                throw new SdkClientException("Unable to store object contents to disk: " + e.getMessage(), e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(outputStream, this.log);
                IOUtils.closeQuietly(s3Object.getObjectContent(), this.log);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(outputStream, this.log);
        IOUtils.closeQuietly(s3Object.getObjectContent(), this.log);
        return s3Object.getObjectMetadata();
    }

    @Override
    final MultipartUploadCryptoContext newUploadContext(InitiateMultipartUploadRequest req, ContentCryptoMaterial cekMaterial) {
        return new MultipartUploadCryptoContext(req.getBucketName(), req.getKey(), cekMaterial);
    }

    @Override
    final CipherLite cipherLiteForNextPart(MultipartUploadCryptoContext uploadContext) {
        return uploadContext.getCipherLite();
    }

    @Override
    final SdkFilterInputStream wrapForMultipart(CipherLiteInputStream is, long partSize) {
        return is;
    }

    @Override
    final long computeLastPartSize(UploadPartRequest req) {
        return req.getPartSize() + (long)(this.contentCryptoScheme.getTagLengthInBits() / 8);
    }

    @Override
    final void updateUploadContext(MultipartUploadCryptoContext uploadContext, SdkFilterInputStream is) {
    }

    private S3ObjectWrapper decrypt(S3ObjectWrapper wrapper, ContentCryptoMaterial cekMaterial, long[] range) {
        S3ObjectInputStream objectContent = wrapper.getObjectContent();
        wrapper.setObjectContent(new S3ObjectInputStream(new CipherLiteInputStream(objectContent, cekMaterial.getCipherLite(), 2048), objectContent.getHttpRequest()));
        return wrapper;
    }

    private void assertParameterNotNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    protected final long ciphertextLength(long originalContentLength) {
        return originalContentLength + (long)(this.contentCryptoScheme.getTagLengthInBits() / 8);
    }

    private void assertCanGetPartialObject() {
        if (!this.isRangeGetEnabled()) {
            String msg = "Unable to perform range get request: Range get support has been disabled. See https://docs.aws.amazon.com/general/latest/gr/aws_sdk_cryptography.html";
            throw new SecurityException(msg);
        }
    }

    protected boolean isRangeGetEnabled() {
        CryptoRangeGetMode rangeGetMode = this.cryptoConfig.getRangeGetMode();
        switch (rangeGetMode) {
            case ALL: {
                return true;
            }
        }
        return false;
    }

    static {
        CryptoRuntime.enableBouncyCastle();
    }
}

