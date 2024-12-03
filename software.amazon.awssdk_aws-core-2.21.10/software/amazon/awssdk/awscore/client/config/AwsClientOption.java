/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.token.credentials.SdkTokenProvider
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.AttributeMap$Key$UnsafeValueType
 */
package software.amazon.awssdk.awscore.client.config;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class AwsClientOption<T>
extends ClientOption<T> {
    @Deprecated
    public static final AwsClientOption<AwsCredentialsProvider> CREDENTIALS_PROVIDER = new AwsClientOption<AwsCredentialsProvider>(AwsCredentialsProvider.class);
    public static final AwsClientOption<IdentityProvider<? extends AwsCredentialsIdentity>> CREDENTIALS_IDENTITY_PROVIDER = new AwsClientOption(new AttributeMap.Key.UnsafeValueType(IdentityProvider.class));
    public static final AwsClientOption<Region> AWS_REGION = new AwsClientOption<Region>(Region.class);
    public static final AwsClientOption<Region> SIGNING_REGION = new AwsClientOption<Region>(Region.class);
    public static final AwsClientOption<Boolean> DUALSTACK_ENDPOINT_ENABLED = new AwsClientOption<Boolean>(Boolean.class);
    public static final ClientOption<Boolean> FIPS_ENDPOINT_ENABLED = new AwsClientOption<Boolean>(Boolean.class);
    public static final AwsClientOption<String> SERVICE_SIGNING_NAME = new AwsClientOption<String>(String.class);
    public static final AwsClientOption<String> ENDPOINT_PREFIX = new AwsClientOption<String>(String.class);
    public static final AwsClientOption<DefaultsMode> DEFAULTS_MODE = new AwsClientOption<DefaultsMode>(DefaultsMode.class);
    public static final AwsClientOption<Boolean> USE_GLOBAL_ENDPOINT = new AwsClientOption<Boolean>(Boolean.class);
    @Deprecated
    public static final AwsClientOption<SdkTokenProvider> TOKEN_PROVIDER = new AwsClientOption<SdkTokenProvider>(SdkTokenProvider.class);
    public static final AwsClientOption<IdentityProvider<? extends TokenIdentity>> TOKEN_IDENTITY_PROVIDER = new AwsClientOption(new AttributeMap.Key.UnsafeValueType(IdentityProvider.class));

    private AwsClientOption(Class<T> valueClass) {
        super(valueClass);
    }

    private AwsClientOption(AttributeMap.Key.UnsafeValueType valueType) {
        super(valueType);
    }
}

