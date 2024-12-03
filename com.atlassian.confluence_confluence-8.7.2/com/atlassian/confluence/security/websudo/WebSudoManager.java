/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security.websudo;

import java.lang.reflect.Method;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface WebSudoManager {
    public boolean isEnabled();

    public boolean matches(String var1, Class<?> var2, Method var3);

    public boolean hasValidSession(@Nullable HttpSession var1);

    public boolean isWebSudoRequest(@Nullable HttpServletRequest var1);

    public void startSession(HttpServletRequest var1, HttpServletResponse var2);

    public void markWebSudoRequest(@Nullable HttpServletRequest var1);

    public void invalidateSession(HttpServletRequest var1, HttpServletResponse var2);

    public URI buildAuthenticationRedirectUri(HttpServletRequest var1);
}

