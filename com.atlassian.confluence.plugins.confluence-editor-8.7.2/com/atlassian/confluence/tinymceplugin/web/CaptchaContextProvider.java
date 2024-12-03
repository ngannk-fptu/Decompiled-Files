/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.tinymceplugin.web;

import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.ArrayList;
import java.util.Map;

public class CaptchaContextProvider
implements ContextProvider {
    private final CaptchaManager captchaManager;

    public CaptchaContextProvider(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        boolean shouldShow = this.captchaManager.showCaptchaForCurrentUser();
        context.put("shouldShow", shouldShow);
        if (shouldShow) {
            Map fieldErrors = (Map)context.get("fieldErrors");
            context.put("captchaId", this.captchaManager.generateCaptchaId());
            context.put("fieldErrors", fieldErrors != null ? fieldErrors.get("captcha") : new ArrayList());
        }
        return context;
    }
}

