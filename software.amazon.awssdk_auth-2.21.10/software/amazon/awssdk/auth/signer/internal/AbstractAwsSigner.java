/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.io.SdkDigestInputStream
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.auth.signer.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.signer.internal.SigningAlgorithm;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.io.SdkDigestInputStream;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public abstract class AbstractAwsSigner
implements Signer {
    private static final ThreadLocal<MessageDigest> SHA256_MESSAGE_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw SdkClientException.builder().message("Unable to get SHA256 Function" + e.getMessage()).cause((Throwable)e).build();
        }
    });

    private static byte[] doHash(String text) throws SdkClientException {
        try {
            MessageDigest md = AbstractAwsSigner.getMessageDigestInstance();
            md.update(text.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to compute hash while signing request: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    private static MessageDigest getMessageDigestInstance() {
        MessageDigest messageDigest = SHA256_MESSAGE_DIGEST.get();
        messageDigest.reset();
        return messageDigest;
    }

    protected String signAndBase64Encode(String data, String key, SigningAlgorithm algorithm) throws SdkClientException {
        return this.signAndBase64Encode(data.getBytes(StandardCharsets.UTF_8), key, algorithm);
    }

    private String signAndBase64Encode(byte[] data, String key, SigningAlgorithm algorithm) throws SdkClientException {
        try {
            byte[] signature = this.sign(data, key.getBytes(StandardCharsets.UTF_8), algorithm);
            return BinaryUtils.toBase64((byte[])signature);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to calculate a request signature: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    protected byte[] signWithMac(String stringData, Mac mac) {
        try {
            return mac.doFinal(stringData.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to calculate a request signature: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    protected byte[] sign(String stringData, byte[] key, SigningAlgorithm algorithm) throws SdkClientException {
        try {
            byte[] data = stringData.getBytes(StandardCharsets.UTF_8);
            return this.sign(data, key, algorithm);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to calculate a request signature: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    protected byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) throws SdkClientException {
        try {
            Mac mac = algorithm.getMac();
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to calculate a request signature: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    static byte[] hash(String text) throws SdkClientException {
        return AbstractAwsSigner.doHash(text);
    }

    byte[] hash(InputStream input, SdkChecksum sdkChecksum) throws SdkClientException {
        try {
            MessageDigest md = AbstractAwsSigner.getMessageDigestInstance();
            SdkDigestInputStream digestInputStream = new SdkDigestInputStream(input, md, sdkChecksum);
            byte[] buffer = new byte[1024];
            while (digestInputStream.read(buffer) > -1) {
            }
            return digestInputStream.getMessageDigest().digest();
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to compute hash while signing request: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    byte[] hash(byte[] data, SdkChecksum sdkChecksum) throws SdkClientException {
        try {
            MessageDigest md = AbstractAwsSigner.getMessageDigestInstance();
            md.update(data);
            if (sdkChecksum != null) {
                sdkChecksum.update(data);
            }
            return md.digest();
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to compute hash while signing request: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    byte[] hash(byte[] data) throws SdkClientException {
        return this.hash(data, null);
    }

    protected InputStream getBinaryRequestPayloadStream(ContentStreamProvider streamProvider) {
        try {
            if (streamProvider == null) {
                return new ByteArrayInputStream(new byte[0]);
            }
            return streamProvider.newStream();
        }
        catch (SdkClientException e) {
            throw e;
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to read request payload to sign request: " + e.getMessage()).cause((Throwable)e).build();
        }
    }

    protected AwsCredentials sanitizeCredentials(AwsCredentials credentials) {
        String accessKeyId = StringUtils.trim((String)credentials.accessKeyId());
        String secretKey = StringUtils.trim((String)credentials.secretAccessKey());
        if (credentials instanceof AwsSessionCredentials) {
            AwsSessionCredentials sessionCredentials = (AwsSessionCredentials)credentials;
            return AwsSessionCredentials.create(accessKeyId, secretKey, StringUtils.trim((String)sessionCredentials.sessionToken()));
        }
        return AwsBasicCredentials.create(accessKeyId, secretKey);
    }

    protected abstract void addSessionCredentials(SdkHttpFullRequest.Builder var1, AwsSessionCredentials var2);
}

