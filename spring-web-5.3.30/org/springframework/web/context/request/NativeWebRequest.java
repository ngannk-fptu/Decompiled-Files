/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

public interface NativeWebRequest
extends WebRequest {
    public Object getNativeRequest();

    @Nullable
    public Object getNativeResponse();

    @Nullable
    public <T> T getNativeRequest(@Nullable Class<T> var1);

    @Nullable
    public <T> T getNativeResponse(@Nullable Class<T> var1);
}

