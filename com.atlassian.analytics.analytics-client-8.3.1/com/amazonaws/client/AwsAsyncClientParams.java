/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.client;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.client.AwsSyncClientParams;
import java.util.concurrent.ExecutorService;

@SdkProtectedApi
public abstract class AwsAsyncClientParams
extends AwsSyncClientParams {
    public abstract ExecutorService getExecutor();
}

