/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;

public interface RequestBodyAdvice {
    public boolean supports(MethodParameter var1, Type var2, Class<? extends HttpMessageConverter<?>> var3);

    public HttpInputMessage beforeBodyRead(HttpInputMessage var1, MethodParameter var2, Type var3, Class<? extends HttpMessageConverter<?>> var4) throws IOException;

    public Object afterBodyRead(Object var1, HttpInputMessage var2, MethodParameter var3, Type var4, Class<? extends HttpMessageConverter<?>> var5);

    @Nullable
    public Object handleEmptyBody(@Nullable Object var1, HttpInputMessage var2, MethodParameter var3, Type var4, Class<? extends HttpMessageConverter<?>> var5);
}

