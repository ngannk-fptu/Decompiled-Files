/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.time.Instant;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

@SdkInternalApi
@Immutable
public final class CredentialScope {
    private final String region;
    private final String service;
    private final Instant instant;

    public CredentialScope(String region, String service, Instant instant) {
        this.region = region;
        this.service = service;
        this.instant = instant;
    }

    public String getRegion() {
        return this.region;
    }

    public String getService() {
        return this.service;
    }

    public Instant getInstant() {
        return this.instant;
    }

    public String getDate() {
        return SignerUtils.formatDate(this.instant);
    }

    public String getDatetime() {
        return SignerUtils.formatDateTime(this.instant);
    }

    public String scope() {
        return this.getDate() + "/" + this.region + "/" + this.service + "/" + "aws4_request";
    }

    public String scope(AwsCredentialsIdentity credentials) {
        return credentials.accessKeyId() + "/" + this.scope();
    }
}

