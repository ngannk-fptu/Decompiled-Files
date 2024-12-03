/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.pats.web.loopsprevention;

import javax.servlet.http.HttpServletRequest;

public interface RedirectsLoopPreventer {
    public void preventRedirectsLoop(HttpServletRequest var1, String var2);
}

