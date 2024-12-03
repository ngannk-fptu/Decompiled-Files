/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

public interface LocaleContextResolver {
    public LocaleContext resolveLocaleContext(ServerWebExchange var1);

    public void setLocaleContext(ServerWebExchange var1, @Nullable LocaleContext var2);
}

