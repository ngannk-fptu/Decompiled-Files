/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.sync;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface SyncPageFetcher<ResponseT> {
    public boolean hasNextPage(ResponseT var1);

    public ResponseT nextPage(ResponseT var1);
}

