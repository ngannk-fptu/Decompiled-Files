/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.async;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface AsyncPageFetcher<ResponseT> {
    public boolean hasNextPage(ResponseT var1);

    public CompletableFuture<ResponseT> nextPage(ResponseT var1);
}

