/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.oauth2.common.web.loopsprevention;

import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import javax.servlet.http.HttpServletRequest;

public class NoopRedirectsLoopPreventer
implements RedirectsLoopPreventer {
    @Override
    public void preventRedirectsLoop(HttpServletRequest request, String target) {
    }
}

