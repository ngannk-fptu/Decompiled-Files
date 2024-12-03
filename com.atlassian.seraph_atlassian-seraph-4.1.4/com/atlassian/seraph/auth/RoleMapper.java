/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.Initable;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public interface RoleMapper
extends Initable {
    public boolean hasRole(Principal var1, HttpServletRequest var2, String var3);

    public boolean canLogin(Principal var1, HttpServletRequest var2);
}

