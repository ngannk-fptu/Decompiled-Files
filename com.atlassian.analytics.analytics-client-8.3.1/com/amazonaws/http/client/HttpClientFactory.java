/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.client;

import com.amazonaws.annotation.Beta;
import com.amazonaws.http.settings.HttpClientSettings;

@Beta
public interface HttpClientFactory<T> {
    public T create(HttpClientSettings var1);
}

