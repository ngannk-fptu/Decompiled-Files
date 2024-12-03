/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.interceptor.ExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.awscore;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.regions.Region;

@SdkPublicApi
public final class AwsExecutionAttribute
extends SdkExecutionAttribute {
    public static final ExecutionAttribute<Region> AWS_REGION = new ExecutionAttribute("AwsRegion");
    public static final ExecutionAttribute<String> ENDPOINT_PREFIX = new ExecutionAttribute("AwsEndpointPrefix");
    public static final ExecutionAttribute<Boolean> DUALSTACK_ENDPOINT_ENABLED = new ExecutionAttribute("DualstackEndpointsEnabled");
    public static final ExecutionAttribute<Boolean> FIPS_ENDPOINT_ENABLED = new ExecutionAttribute("FipsEndpointsEnabled");
    public static final ExecutionAttribute<Boolean> USE_GLOBAL_ENDPOINT = new ExecutionAttribute("UseGlobalEndpoint");

    private AwsExecutionAttribute() {
    }
}

