/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.elevatedsecurity;

import com.atlassian.seraph.Initable;
import javax.servlet.http.HttpServletRequest;

public interface ElevatedSecurityGuard
extends Initable {
    public boolean performElevatedSecurityCheck(HttpServletRequest var1, String var2);

    public void onFailedLoginAttempt(HttpServletRequest var1, String var2);

    public void onSuccessfulLoginAttempt(HttpServletRequest var1, String var2);
}

