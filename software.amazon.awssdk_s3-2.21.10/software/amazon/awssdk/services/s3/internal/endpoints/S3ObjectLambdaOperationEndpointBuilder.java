/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class S3ObjectLambdaOperationEndpointBuilder {
    private String region;
    private String protocol;
    private String domain;

    private S3ObjectLambdaOperationEndpointBuilder() {
    }

    public static S3ObjectLambdaOperationEndpointBuilder create() {
        return new S3ObjectLambdaOperationEndpointBuilder();
    }

    public S3ObjectLambdaOperationEndpointBuilder region(String region) {
        this.region = region;
        return this;
    }

    public S3ObjectLambdaOperationEndpointBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public S3ObjectLambdaOperationEndpointBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public URI toUri() {
        Validate.paramNotBlank((CharSequence)this.protocol, (String)"protocol");
        Validate.paramNotBlank((CharSequence)this.domain, (String)"domain");
        Validate.paramNotBlank((CharSequence)this.region, (String)"region");
        String servicePrefix = "s3-object-lambda";
        String uriString = String.format("%s://%s.%s.%s", this.protocol, servicePrefix, this.region, this.domain);
        return URI.create(uriString);
    }
}

