/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.CredentialType
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.internal.signer.SigningMethod
 *  software.amazon.awssdk.core.signer.NoOpSigner
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.auth.signer.internal.util;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.S3SignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.internal.AbstractAwsS3V4Signer;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.signer.SigningMethod;
import software.amazon.awssdk.core.signer.NoOpSigner;
import software.amazon.awssdk.core.signer.Signer;

@SdkInternalApi
public final class SignerMethodResolver {
    public static final String S3_SIGV4A_SIGNER_CLASS_PATH = "software.amazon.awssdk.authcrt.signer.internal.DefaultAwsCrtS3V4aSigner";

    private SignerMethodResolver() {
    }

    public static SigningMethod resolveSigningMethodUsed(Signer signer, ExecutionAttributes executionAttributes, AwsCredentials credentials) {
        SigningMethod signingMethod = SigningMethod.UNSIGNED_PAYLOAD;
        if (signer != null && !CredentialType.TOKEN.equals((Object)signer.credentialType())) {
            signingMethod = SignerMethodResolver.isProtocolBasedStreamingSigningAuth(signer, executionAttributes) ? SigningMethod.PROTOCOL_STREAMING_SIGNING_AUTH : (SignerMethodResolver.isProtocolBasedUnsigned(signer, executionAttributes) ? SigningMethod.PROTOCOL_BASED_UNSIGNED : (SignerMethodResolver.isAnonymous(credentials) || signer instanceof NoOpSigner ? SigningMethod.UNSIGNED_PAYLOAD : SigningMethod.HEADER_BASED_AUTH));
        }
        return signingMethod;
    }

    private static boolean isProtocolBasedStreamingSigningAuth(Signer signer, ExecutionAttributes executionAttributes) {
        return executionAttributes.getOptionalAttribute(S3SignerExecutionAttribute.ENABLE_PAYLOAD_SIGNING).orElse(false) != false && executionAttributes.getOptionalAttribute(S3SignerExecutionAttribute.ENABLE_CHUNKED_ENCODING).orElse(false) != false || SignerMethodResolver.supportsPayloadSigning(signer) && executionAttributes.getOptionalAttribute(S3SignerExecutionAttribute.ENABLE_CHUNKED_ENCODING).orElse(false) != false;
    }

    private static boolean supportsPayloadSigning(Signer signer) {
        if (signer == null) {
            return false;
        }
        return signer instanceof AbstractAwsS3V4Signer || S3_SIGV4A_SIGNER_CLASS_PATH.equals(signer.getClass().getCanonicalName());
    }

    private static boolean isProtocolBasedUnsigned(Signer signer, ExecutionAttributes executionAttributes) {
        return signer instanceof Aws4UnsignedPayloadSigner || signer instanceof AwsS3V4Signer || executionAttributes.getOptionalAttribute(S3SignerExecutionAttribute.ENABLE_PAYLOAD_SIGNING).orElse(false) != false;
    }

    public static boolean isAnonymous(AwsCredentials credentials) {
        return credentials.secretAccessKey() == null && credentials.accessKeyId() == null;
    }
}

