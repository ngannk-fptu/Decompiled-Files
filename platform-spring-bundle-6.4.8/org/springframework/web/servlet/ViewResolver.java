/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet;

import java.util.Locale;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.View;

public interface ViewResolver {
    @Nullable
    public View resolveViewName(String var1, Locale var2) throws Exception;
}

