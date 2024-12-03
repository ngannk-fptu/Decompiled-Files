/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.confluence.xwork.RedirectResult;
import com.opensymphony.xwork2.ActionInvocation;

public class RedirectWithFlashResult
extends RedirectResult {
    @Override
    protected void doExecute(String redirectUrl, ActionInvocation actionInvocation) throws Exception {
        String flashId = FlashScope.persist();
        String redirectUrlWithFlashHash = FlashScope.getFlashScopeUrl(redirectUrl, flashId);
        super.doExecute(redirectUrlWithFlashHash, actionInvocation);
    }
}

