/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.metrics.MetricCategory
 *  software.amazon.awssdk.metrics.MetricLevel
 *  software.amazon.awssdk.metrics.SdkMetric
 */
package software.amazon.awssdk.core.metrics;

import java.net.URI;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCategory;
import software.amazon.awssdk.metrics.MetricLevel;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkPublicApi
public final class CoreMetric {
    public static final SdkMetric<String> SERVICE_ID = CoreMetric.metric("ServiceId", String.class, MetricLevel.ERROR);
    public static final SdkMetric<String> OPERATION_NAME = CoreMetric.metric("OperationName", String.class, MetricLevel.ERROR);
    public static final SdkMetric<Boolean> API_CALL_SUCCESSFUL = CoreMetric.metric("ApiCallSuccessful", Boolean.class, MetricLevel.ERROR);
    public static final SdkMetric<Integer> RETRY_COUNT = CoreMetric.metric("RetryCount", Integer.class, MetricLevel.ERROR);
    public static final SdkMetric<URI> SERVICE_ENDPOINT = CoreMetric.metric("ServiceEndpoint", URI.class, MetricLevel.ERROR);
    public static final SdkMetric<Duration> API_CALL_DURATION = CoreMetric.metric("ApiCallDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> CREDENTIALS_FETCH_DURATION = CoreMetric.metric("CredentialsFetchDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> TOKEN_FETCH_DURATION = CoreMetric.metric("TokenFetchDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> BACKOFF_DELAY_DURATION = CoreMetric.metric("BackoffDelayDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> MARSHALLING_DURATION = CoreMetric.metric("MarshallingDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> SIGNING_DURATION = CoreMetric.metric("SigningDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> SERVICE_CALL_DURATION = CoreMetric.metric("ServiceCallDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<Duration> UNMARSHALLING_DURATION = CoreMetric.metric("UnmarshallingDuration", Duration.class, MetricLevel.INFO);
    public static final SdkMetric<String> AWS_REQUEST_ID = CoreMetric.metric("AwsRequestId", String.class, MetricLevel.INFO);
    public static final SdkMetric<String> AWS_EXTENDED_REQUEST_ID = CoreMetric.metric("AwsExtendedRequestId", String.class, MetricLevel.INFO);
    public static final SdkMetric<String> ERROR_TYPE = CoreMetric.metric("ErrorType", String.class, MetricLevel.INFO);

    private CoreMetric() {
    }

    private static <T> SdkMetric<T> metric(String name, Class<T> clzz, MetricLevel level) {
        return SdkMetric.create((String)name, clzz, (MetricLevel)level, (MetricCategory)MetricCategory.CORE, (MetricCategory[])new MetricCategory[0]);
    }
}

