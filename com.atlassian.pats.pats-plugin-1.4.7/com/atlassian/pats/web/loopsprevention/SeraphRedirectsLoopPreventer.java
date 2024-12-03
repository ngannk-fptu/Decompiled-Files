/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.pats.web.loopsprevention;

import com.atlassian.pats.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.seraph.config.SecurityConfigFactory;
import javax.servlet.http.HttpServletRequest;

public class SeraphRedirectsLoopPreventer
implements RedirectsLoopPreventer {
    @Override
    public void preventRedirectsLoop(HttpServletRequest request, String target) {
        String originalURLKey = SecurityConfigFactory.getInstance().getOriginalURLKey();
        request.getSession().setAttribute(originalURLKey, (Object)target);
    }
}

