/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.octo.captcha.module.config;

import com.octo.captcha.module.config.CaptchaModuleConfig;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;

public class CaptchaModuleConfigHelper {
    public static String getId(HttpServletRequest httpServletRequest) {
        boolean generatedId = CaptchaModuleConfig.getInstance().getIdType().equals("generated");
        String captchaID = generatedId ? httpServletRequest.getParameter(CaptchaModuleConfig.getInstance().getIdKey()) : httpServletRequest.getSession().getId();
        return captchaID;
    }

    public static String getMessage(HttpServletRequest httpServletRequest) {
        boolean messageBundle;
        String message = null;
        boolean bl = messageBundle = CaptchaModuleConfig.getInstance().getMessageType().equals("bundle");
        if (messageBundle) {
            ResourceBundle bundle = ResourceBundle.getBundle(CaptchaModuleConfig.getInstance().getMessageValue(), httpServletRequest.getLocale());
            if (bundle != null) {
                message = bundle.getString(CaptchaModuleConfig.getInstance().getMessageKey());
            }
            if (message == null) {
                bundle = ResourceBundle.getBundle(CaptchaModuleConfig.getInstance().getMessageValue());
                message = bundle.getString(CaptchaModuleConfig.getInstance().getMessageKey());
            }
        } else {
            message = CaptchaModuleConfig.getInstance().getMessageValue();
        }
        return message;
    }
}

