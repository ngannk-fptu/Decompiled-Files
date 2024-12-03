/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.loopsprevention;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugins.authentication.impl.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.seraph.config.SecurityConfigFactory;
import javax.servlet.http.HttpServletRequest;

@JiraComponent
@ConfluenceComponent
@BambooComponent
public class SeraphRedirectsLoopPreventer
implements RedirectsLoopPreventer {
    @Override
    public void preventRedirectsLoop(HttpServletRequest request, String target) {
        String originalURLKey = SecurityConfigFactory.getInstance().getOriginalURLKey();
        request.getSession().setAttribute(originalURLKey, (Object)target);
    }
}

