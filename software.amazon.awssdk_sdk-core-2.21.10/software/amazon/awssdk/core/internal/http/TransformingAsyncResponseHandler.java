/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler
 */
package software.amazon.awssdk.core.internal.http;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;

@SdkInternalApi
public interface TransformingAsyncResponseHandler<ResultT>
extends SdkAsyncHttpResponseHandler {
    public CompletableFuture<ResultT> prepare();
}

