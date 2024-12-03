/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.servlet.AbstractNoOpServlet
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.servlet.AbstractNoOpServlet;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceNoOpServlet
extends AbstractNoOpServlet {
    protected String getUserName(HttpServletRequest httpServletRequest) {
        return AuthenticatedUserThreadLocal.getUsername();
    }
}

