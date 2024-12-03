/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.profiles;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public final class ProfileProperty {
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    public static final String AWS_SESSION_TOKEN = "aws_session_token";
    public static final String ROLE_ARN = "role_arn";
    public static final String ROLE_SESSION_NAME = "role_session_name";
    public static final String DURATION_SECONDS = "duration_seconds";
    public static final String EXTERNAL_ID = "external_id";
    public static final String SOURCE_PROFILE = "source_profile";
    public static final String CREDENTIAL_SOURCE = "credential_source";
    public static final String REGION = "region";
    public static final String MFA_SERIAL = "mfa_serial";
    public static final String ENDPOINT_DISCOVERY_ENABLED = "aws_endpoint_discovery_enabled";
    public static final String CREDENTIAL_PROCESS = "credential_process";
    public static final String WEB_IDENTITY_TOKEN_FILE = "web_identity_token_file";
    public static final String S3_US_EAST_1_REGIONAL_ENDPOINT = "s3_us_east_1_regional_endpoint";
    public static final String RETRY_MODE = "retry_mode";
    public static final String DEFAULTS_MODE = "defaults_mode";
    public static final String SSO_REGION = "sso_region";
    public static final String SSO_ROLE_NAME = "sso_role_name";
    public static final String SSO_ACCOUNT_ID = "sso_account_id";
    public static final String SSO_START_URL = "sso_start_url";
    public static final String USE_DUALSTACK_ENDPOINT = "use_dualstack_endpoint";
    public static final String USE_FIPS_ENDPOINT = "use_fips_endpoint";
    public static final String EC2_METADATA_SERVICE_ENDPOINT_MODE = "ec2_metadata_service_endpoint_mode";
    public static final String EC2_METADATA_SERVICE_ENDPOINT = "ec2_metadata_service_endpoint";
    public static final String DISABLE_REQUEST_COMPRESSION = "disable_request_compression";
    public static final String REQUEST_MIN_COMPRESSION_SIZE_BYTES = "request_min_compression_size_bytes";

    private ProfileProperty() {
    }
}

