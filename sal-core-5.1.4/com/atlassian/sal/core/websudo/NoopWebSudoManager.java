/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.core.websudo;

import com.atlassian.sal.api.websudo.WebSudoManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoopWebSudoManager
implements WebSudoManager {
    public boolean canExecuteRequest(HttpServletRequest request) {
        return true;
    }

    public void enforceWebSudoProtection(HttpServletRequest request, HttpServletResponse response) {
    }

    public void willExecuteWebSudoRequest(HttpServletRequest request) {
    }
}

