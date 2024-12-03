/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.HostnameValidator
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.HostnameValidator;

@SdkInternalApi
public class S3ObjectLambdaEndpointBuilder {
    private URI endpointOverride;
    private String accessPointName;
    private String region;
    private String accountId;
    private String protocol;
    private String domain;
    private Boolean fipsEnabled;
    private Boolean dualstackEnabled;

    private S3ObjectLambdaEndpointBuilder() {
    }

    public static S3ObjectLambdaEndpointBuilder create() {
        return new S3ObjectLambdaEndpointBuilder();
    }

    public S3ObjectLambdaEndpointBuilder endpointOverride(URI endpointOverride) {
        this.endpointOverride = endpointOverride;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder accessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder region(String region) {
        this.region = region;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder fipsEnabled(Boolean fipsEnabled) {
        this.fipsEnabled = fipsEnabled;
        return this;
    }

    public S3ObjectLambdaEndpointBuilder dualstackEnabled(Boolean dualstackEnabled) {
        this.dualstackEnabled = dualstackEnabled;
        return this;
    }

    public URI toUri() {
        String uriString;
        String fipsSegment;
        HostnameValidator.validateHostnameCompliant((String)this.accountId, (String)"accountId", (String)"object lambda ARN");
        HostnameValidator.validateHostnameCompliant((String)this.accessPointName, (String)"accessPointName", (String)"object lambda ARN");
        String string = fipsSegment = Boolean.TRUE.equals(this.fipsEnabled) ? "-fips" : "";
        if (this.endpointOverride == null) {
            if (Boolean.TRUE.equals(this.dualstackEnabled)) {
                throw new IllegalStateException("S3 Object Lambda does not support Dual stack endpoints.");
            }
            uriString = String.format("%s://%s-%s.s3-object-lambda%s.%s.%s", this.protocol, this.accessPointName, this.accountId, fipsSegment, this.region, this.domain);
        } else {
            StringBuilder uriSuffix = new StringBuilder(this.endpointOverride.getHost());
            if (this.endpointOverride.getPort() > 0) {
                uriSuffix.append(":").append(this.endpointOverride.getPort());
            }
            if (this.endpointOverride.getPath() != null) {
                uriSuffix.append(this.endpointOverride.getPath());
            }
            uriString = String.format("%s://%s-%s.%s", this.protocol, this.accessPointName, this.accountId, uriSuffix);
        }
        return URI.create(uriString);
    }
}

