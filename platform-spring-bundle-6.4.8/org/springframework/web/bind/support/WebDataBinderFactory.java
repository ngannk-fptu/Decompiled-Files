/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

public interface WebDataBinderFactory {
    public WebDataBinder createBinder(NativeWebRequest var1, @Nullable Object var2, String var3) throws Exception;
}

