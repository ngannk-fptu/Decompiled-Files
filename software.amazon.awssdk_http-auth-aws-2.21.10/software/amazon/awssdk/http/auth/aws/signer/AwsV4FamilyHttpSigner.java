/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.http.auth.spi.signer.SignerProperty
 *  software.amazon.awssdk.identity.spi.Identity
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
    public static final SignerProperty<String> SERVICE_SIGNING_NAME = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"ServiceSigningName");
    public static final SignerProperty<Boolean> DOUBLE_URL_ENCODE = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"DoubleUrlEncode");
    public static final SignerProperty<Boolean> NORMALIZE_PATH = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"NormalizePath");
    public static final SignerProperty<AuthLocation> AUTH_LOCATION = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"AuthLocation");
    public static final SignerProperty<Duration> EXPIRATION_DURATION = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"ExpirationDuration");
    public static final SignerProperty<Boolean> PAYLOAD_SIGNING_ENABLED = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"PayloadSigningEnabled");
    public static final SignerProperty<Boolean> CHUNK_ENCODING_ENABLED = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"ChunkEncodingEnabled");
    public static final SignerProperty<ChecksumAlgorithm> CHECKSUM_ALGORITHM = SignerProperty.create(AwsV4FamilyHttpSigner.class, (String)"ChecksumAlgorithm");

    public static enum AuthLocation {
        HEADER,
        QUERY_STRING;

    }
}

