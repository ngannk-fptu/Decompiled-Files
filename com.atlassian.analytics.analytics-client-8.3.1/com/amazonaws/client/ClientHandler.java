/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.client;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.client.ClientExecutionParams;

@SdkProtectedApi
public abstract class ClientHandler {
    public abstract <Input, Output> Output execute(ClientExecutionParams<Input, Output> var1);

    public abstract void shutdown();
}

