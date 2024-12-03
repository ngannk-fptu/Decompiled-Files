/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.utils.HostnameValidator
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.HostnameValidator;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class S3OutpostAccessPointBuilder {
    private URI endpointOverride;
    private String accessPointName;
    private String outpostId;
    private String region;
    private String accountId;
    private String protocol;
    private String domain;

    private S3OutpostAccessPointBuilder() {
    }

    public static S3OutpostAccessPointBuilder create() {
        return new S3OutpostAccessPointBuilder();
    }

    public S3OutpostAccessPointBuilder endpointOverride(URI endpointOverride) {
        this.endpointOverride = endpointOverride;
        return this;
    }

    public S3OutpostAccessPointBuilder accessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }

    public S3OutpostAccessPointBuilder region(String region) {
        this.region = region;
        return this;
    }

    public S3OutpostAccessPointBuilder accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public S3OutpostAccessPointBuilder outpostId(String outpostId) {
        this.outpostId = outpostId;
        return this;
    }

    public S3OutpostAccessPointBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public S3OutpostAccessPointBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public URI toUri() {
        String uri;
        HostnameValidator.validateHostnameCompliant((String)this.outpostId, (String)"outpostId", (String)"outpost ARN");
        HostnameValidator.validateHostnameCompliant((String)this.accountId, (String)"accountId", (String)"outpost ARN");
        HostnameValidator.validateHostnameCompliant((String)this.accessPointName, (String)"accessPointName", (String)"outpost ARN");
        if (this.endpointOverride == null) {
            uri = String.format("%s://%s-%s.%s.s3-outposts.%s.%s", this.protocol, this.accessPointName, this.accountId, this.outpostId, this.region, this.domain);
        } else {
            StringBuilder uriSuffix = new StringBuilder(this.endpointOverride.getHost());
            if (this.endpointOverride.getPort() > 0) {
                uriSuffix.append(":").append(this.endpointOverride.getPort());
            }
            if (this.endpointOverride.getPath() != null) {
                uriSuffix.append(this.endpointOverride.getPath());
            }
            uri = String.format("%s://%s-%s.%s.%s", this.protocol, SdkHttpUtils.urlEncode((String)this.accessPointName), this.accountId, this.outpostId, uriSuffix);
        }
        URI result = URI.create(uri);
        if (result.getHost() == null) {
            throw SdkClientException.create((String)("Request resulted in an invalid URI: " + result));
        }
        return result;
    }
}

