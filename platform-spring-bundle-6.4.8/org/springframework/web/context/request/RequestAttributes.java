/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

public interface RequestAttributes {
    public static final int SCOPE_REQUEST = 0;
    public static final int SCOPE_SESSION = 1;
    public static final String REFERENCE_REQUEST = "request";
    public static final String REFERENCE_SESSION = "session";

    @Nullable
    public Object getAttribute(String var1, int var2);

    public void setAttribute(String var1, Object var2, int var3);

    public void removeAttribute(String var1, int var2);

    public String[] getAttributeNames(int var1);

    public void registerDestructionCallback(String var1, Runnable var2, int var3);

    @Nullable
    public Object resolveReference(String var1);

    public String getSessionId();

    public Object getSessionMutex();
}

