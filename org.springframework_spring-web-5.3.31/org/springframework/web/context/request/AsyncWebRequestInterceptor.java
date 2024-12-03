/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public interface AsyncWebRequestInterceptor
extends WebRequestInterceptor {
    public void afterConcurrentHandlingStarted(WebRequest var1);
}

