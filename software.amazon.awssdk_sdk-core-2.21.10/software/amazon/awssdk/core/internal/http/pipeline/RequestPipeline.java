/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.http.pipeline;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;

@SdkInternalApi
public interface RequestPipeline<InputT, OutputT> {
    public OutputT execute(InputT var1, RequestExecutionContext var2) throws Exception;
}

