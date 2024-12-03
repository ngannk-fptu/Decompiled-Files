/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public interface MutableRequestToRequestPipeline
extends RequestPipeline<SdkHttpFullRequest.Builder, SdkHttpFullRequest.Builder> {
}

