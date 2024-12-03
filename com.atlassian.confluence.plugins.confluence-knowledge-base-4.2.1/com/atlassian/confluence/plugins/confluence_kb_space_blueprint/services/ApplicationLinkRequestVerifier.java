/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ApplicationLinkRequestVerifier {
    public boolean isApplicationLinkRequest(HttpServletRequest var1, HttpServletResponse var2);
}

