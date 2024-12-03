/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor;

import java.security.Principal;

public interface PrincipalProxy {
    public boolean isUserInRole(String var1);

    public Principal getUserPrincipal();

    public String getRemoteUser();

    public boolean isRequestSecure();
}

