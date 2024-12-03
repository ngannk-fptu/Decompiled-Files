/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ModelMap
 */
package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

public interface WebRequestInterceptor {
    public void preHandle(WebRequest var1) throws Exception;

    public void postHandle(WebRequest var1, @Nullable ModelMap var2) throws Exception;

    public void afterCompletion(WebRequest var1, @Nullable Exception var2) throws Exception;
}

