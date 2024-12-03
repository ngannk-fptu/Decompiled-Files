/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.signer;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignerProperty;
import software.amazon.awssdk.identity.spi.Identity;

@SdkPublicApi
public interface AwsV4FamilyHttpSigner<T extends Identity>
extends HttpSigner<T> {
    public static final SignerProperty<String> SERVICE_SIGNING_NAME = SignerProperty.create(AwsV4FamilyHttpSigner.class, "ServiceSigningName");
    public static final SignerProperty<Boolean> DOUBLE_URL_ENCODE = SignerProperty.create(AwsV4FamilyHttpSigner.class, "DoubleUrlEncode");
    public static final SignerProperty<Boolean> NORMALIZE_PATH = SignerProperty.create(AwsV4FamilyHttpSigner.class, "NormalizePath");
    public static final SignerProperty<AuthLocation> AUTH_LOCATION = SignerProperty.create(AwsV4FamilyHttpSigner.class, "AuthLocation");
    public static final SignerProperty<Duration> EXPIRATION_DURATION = SignerProperty.create(AwsV4FamilyHttpSigner.class, "ExpirationDuration");
    public static final SignerProperty<Boolean> PAYLOAD_SIGNING_ENABLED = SignerProperty.create(AwsV4FamilyHttpSigner.class, "PayloadSigningEnabled");
    public static final SignerProperty<Boolean> CHUNK_ENCODING_ENABLED = SignerProperty.create(AwsV4FamilyHttpSigner.class, "ChunkEncodingEnabled");
    public static final SignerProperty<ChecksumAlgorithm> CHECKSUM_ALGORITHM = SignerProperty.create(AwsV4FamilyHttpSigner.class, "ChecksumAlgorithm");

    public static enum AuthLocation {
        HEADER,
        QUERY_STRING;

    }
}

