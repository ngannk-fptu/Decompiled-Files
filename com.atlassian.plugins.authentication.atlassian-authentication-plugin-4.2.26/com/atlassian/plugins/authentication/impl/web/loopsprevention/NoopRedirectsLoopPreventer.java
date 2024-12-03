/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.loopsprevention;

import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugins.authentication.impl.web.loopsprevention.RedirectsLoopPreventer;
import javax.servlet.http.HttpServletRequest;

@BitbucketComponent
public class NoopRedirectsLoopPreventer
implements RedirectsLoopPreventer {
    @Override
    public void preventRedirectsLoop(HttpServletRequest request, String target) {
    }
}

