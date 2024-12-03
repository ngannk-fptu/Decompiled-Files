/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.api.websudo;

import com.atlassian.sal.api.websudo.WebSudoSessionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebSudoManager {
    public boolean canExecuteRequest(HttpServletRequest var1);

    public void enforceWebSudoProtection(HttpServletRequest var1, HttpServletResponse var2);

    public void willExecuteWebSudoRequest(HttpServletRequest var1) throws WebSudoSessionException;
}

