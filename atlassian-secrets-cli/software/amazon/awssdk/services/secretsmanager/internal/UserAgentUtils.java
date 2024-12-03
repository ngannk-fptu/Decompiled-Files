/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.internal;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;

@SdkInternalApi
public class UserAgentUtils {
    private UserAgentUtils() {
    }

    public static <T extends SecretsManagerRequest> T applyUserAgentInfo(T request, Consumer<AwsRequestOverrideConfiguration.Builder> userAgentApplier) {
        AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(userAgentApplier)).build()).orElse(AwsRequestOverrideConfiguration.builder().applyMutation(userAgentApplier).build());
        return (T)((SecretsManagerRequest)request.toBuilder().overrideConfiguration(overrideConfiguration).build());
    }

    public static <T extends SecretsManagerRequest> T applyPaginatorUserAgent(T request) {
        return UserAgentUtils.applyUserAgentInfo(request, b -> {
            AwsRequestOverrideConfiguration.Builder cfr_ignored_0 = (AwsRequestOverrideConfiguration.Builder)b.addApiName(ApiName.builder().version("2.21.15").name("PAGINATED").build());
        });
    }
}

