/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.impl.xsrf;

import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface XsrfTokenValidationManager {
    public boolean isRequestExempt(String var1, HttpServletRequest var2);

    public boolean isRequestValid(Class<?> var1, Method var2, Map<String, String> var3, HttpServletRequest var4);
}

