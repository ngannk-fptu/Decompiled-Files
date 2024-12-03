/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;

public interface WebRequest
extends RequestAttributes {
    @Nullable
    public String getHeader(String var1);

    @Nullable
    public String[] getHeaderValues(String var1);

    public Iterator<String> getHeaderNames();

    @Nullable
    public String getParameter(String var1);

    @Nullable
    public String[] getParameterValues(String var1);

    public Iterator<String> getParameterNames();

    public Map<String, String[]> getParameterMap();

    public Locale getLocale();

    public String getContextPath();

    @Nullable
    public String getRemoteUser();

    @Nullable
    public Principal getUserPrincipal();

    public boolean isUserInRole(String var1);

    public boolean isSecure();

    public boolean checkNotModified(long var1);

    public boolean checkNotModified(String var1);

    public boolean checkNotModified(@Nullable String var1, long var2);

    public String getDescription(boolean var1);
}

