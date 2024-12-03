/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.ApiName
 */
package software.amazon.awssdk.services.sts.internal;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.services.sts.model.StsRequest;

@SdkInternalApi
public class UserAgentUtils {
    private UserAgentUtils() {
    }

    public static <T extends StsRequest> T applyUserAgentInfo(T request, Consumer<AwsRequestOverrideConfiguration.Builder> userAgentApplier) {
        AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().map(c -> ((AwsRequestOverrideConfiguration.Builder)c.toBuilder().applyMutation(userAgentApplier)).build()).orElse(((AwsRequestOverrideConfiguration.Builder)AwsRequestOverrideConfiguration.builder().applyMutation(userAgentApplier)).build());
        return (T)((Object)((StsRequest)request.toBuilder().overrideConfiguration(overrideConfiguration).build()));
    }

    public static <T extends StsRequest> T applyPaginatorUserAgent(T request) {
        return UserAgentUtils.applyUserAgentInfo(request, b -> {
            AwsRequestOverrideConfiguration.Builder cfr_ignored_0 = (AwsRequestOverrideConfiguration.Builder)b.addApiName(ApiName.builder().version("2.21.10").name("PAGINATED").build());
        });
    }
}

