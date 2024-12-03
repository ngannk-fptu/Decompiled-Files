/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.Initable;
import javax.servlet.http.HttpServletRequest;

public interface RedirectPolicy
extends Initable {
    public boolean allowedRedirectDestination(String var1, HttpServletRequest var2);
}

