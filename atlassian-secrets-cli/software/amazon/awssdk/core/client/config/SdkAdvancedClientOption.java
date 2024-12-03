/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.config;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.signer.Signer;

@SdkPublicApi
public class SdkAdvancedClientOption<T>
extends ClientOption<T> {
    public static final SdkAdvancedClientOption<String> USER_AGENT_PREFIX = new SdkAdvancedClientOption<String>(String.class);
    public static final SdkAdvancedClientOption<String> USER_AGENT_SUFFIX = new SdkAdvancedClientOption<String>(String.class);
    public static final SdkAdvancedClientOption<Signer> SIGNER = new SdkAdvancedClientOption<Signer>(Signer.class);
    public static final SdkAdvancedClientOption<Signer> TOKEN_SIGNER = new SdkAdvancedClientOption<Signer>(Signer.class);
    public static final SdkAdvancedClientOption<Boolean> DISABLE_HOST_PREFIX_INJECTION = new SdkAdvancedClientOption<Boolean>(Boolean.class);

    protected SdkAdvancedClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

