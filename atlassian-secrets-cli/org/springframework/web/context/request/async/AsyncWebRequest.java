/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

public interface AsyncWebRequest
extends NativeWebRequest {
    public void setTimeout(@Nullable Long var1);

    public void addTimeoutHandler(Runnable var1);

    public void addErrorHandler(Consumer<Throwable> var1);

    public void addCompletionHandler(Runnable var1);

    public void startAsync();

    public boolean isAsyncStarted();

    public void dispatch();

    public boolean isAsyncComplete();
}

