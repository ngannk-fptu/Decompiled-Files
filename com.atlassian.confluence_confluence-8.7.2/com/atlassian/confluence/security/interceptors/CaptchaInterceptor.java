/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.security.interceptors;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import java.util.function.Supplier;

public class CaptchaInterceptor
extends AbstractAwareInterceptor {
    private final Supplier<CaptchaManager> captchaManagerSupplier = new LazyComponentReference("captchaManager");

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof CaptchaAware && action instanceof ConfluenceActionSupport && ContainerManager.isContainerSetup()) {
            CaptchaAware captchaAwareAction = (CaptchaAware)action;
            CaptchaManager captchaManager = this.getCaptchaManager();
            if (captchaManager.isCaptchaEnabled() || captchaAwareAction.mustValidateCaptcha()) {
                boolean isCaptchaValidated;
                String captchaId = this.getParameter("captchaId");
                String captchaResponse = this.getParameter("captchaResponse");
                boolean bl = isCaptchaValidated = captchaAwareAction.mustValidateCaptcha() ? captchaManager.forceValidateCaptcha(captchaId, captchaResponse) : captchaManager.validateCaptcha(captchaId, captchaResponse);
                if (!isCaptchaValidated) {
                    ConfluenceActionSupport confluenceAction = (ConfluenceActionSupport)action;
                    confluenceAction.addFieldError("captcha", "captcha.response.failed", new String[0]);
                    return "input";
                }
            }
        }
        return actionInvocation.invoke();
    }

    private CaptchaManager getCaptchaManager() {
        return this.captchaManagerSupplier.get();
    }
}

