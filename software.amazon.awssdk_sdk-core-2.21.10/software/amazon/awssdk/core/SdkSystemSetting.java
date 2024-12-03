/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.SystemSetting
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SystemSetting;

@SdkProtectedApi
public enum SdkSystemSetting implements SystemSetting
{
    AWS_ACCESS_KEY_ID("aws.accessKeyId", null),
    AWS_SECRET_ACCESS_KEY("aws.secretAccessKey", null),
    AWS_SESSION_TOKEN("aws.sessionToken", null),
    AWS_WEB_IDENTITY_TOKEN_FILE("aws.webIdentityTokenFile", null),
    AWS_ROLE_ARN("aws.roleArn", null),
    AWS_ROLE_SESSION_NAME("aws.roleSessionName", null),
    AWS_REGION("aws.region", null),
    AWS_EC2_METADATA_DISABLED("aws.disableEc2Metadata", "false"),
    AWS_EC2_METADATA_SERVICE_ENDPOINT("aws.ec2MetadataServiceEndpoint", "http://169.254.169.254"),
    AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE("aws.ec2MetadataServiceEndpointMode", "IPv4"),
    AWS_CONTAINER_SERVICE_ENDPOINT("aws.containerServiceEndpoint", "http://169.254.170.2"),
    AWS_CONTAINER_CREDENTIALS_RELATIVE_URI("aws.containerCredentialsPath", null),
    AWS_CONTAINER_CREDENTIALS_FULL_URI("aws.containerCredentialsFullUri", null),
    AWS_CONTAINER_AUTHORIZATION_TOKEN("aws.containerAuthorizationToken", null),
    SYNC_HTTP_SERVICE_IMPL("software.amazon.awssdk.http.service.impl", null),
    ASYNC_HTTP_SERVICE_IMPL("software.amazon.awssdk.http.async.service.impl", null),
    CBOR_ENABLED("aws.cborEnabled", "true"),
    BINARY_ION_ENABLED("aws.binaryIonEnabled", "true"),
    AWS_EXECUTION_ENV("aws.executionEnvironment", null),
    AWS_ENDPOINT_DISCOVERY_ENABLED("aws.endpointDiscoveryEnabled", null),
    AWS_S3_US_EAST_1_REGIONAL_ENDPOINT("aws.s3UseUsEast1RegionalEndpoint", null),
    AWS_RETRY_MODE("aws.retryMode", null),
    AWS_MAX_ATTEMPTS("aws.maxAttempts", null),
    AWS_DEFAULTS_MODE("aws.defaultsMode", null),
    AWS_USE_DUALSTACK_ENDPOINT("aws.useDualstackEndpoint", null),
    AWS_USE_FIPS_ENDPOINT("aws.useFipsEndpoint", null),
    AWS_DISABLE_REQUEST_COMPRESSION("aws.disableRequestCompression", null),
    AWS_REQUEST_MIN_COMPRESSION_SIZE_BYTES("aws.requestMinCompressionSizeBytes", null);

    private final String systemProperty;
    private final String defaultValue;

    private SdkSystemSetting(String systemProperty, String defaultValue) {
        this.systemProperty = systemProperty;
        this.defaultValue = defaultValue;
    }

    public String property() {
        return this.systemProperty;
    }

    public String environmentVariable() {
        return this.name();
    }

    public String defaultValue() {
        return this.defaultValue;
    }
}

