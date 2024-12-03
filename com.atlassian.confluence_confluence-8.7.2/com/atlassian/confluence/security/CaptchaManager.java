/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.image.ImageCaptchaService
 */
package com.atlassian.confluence.security;

import com.octo.captcha.service.image.ImageCaptchaService;
import java.util.Collection;

public interface CaptchaManager {
    public static final String DEBUG_CAPTCHA = "DEBUG";

    public boolean validateCaptcha(String var1, String var2);

    public boolean forceValidateCaptcha(String var1, String var2);

    public boolean isCaptchaEnabled();

    public void setCaptchaEnabled(boolean var1);

    public boolean isDebugEnabled();

    public void setDebugMode(boolean var1);

    public String getExclude();

    public void setExclude(String var1);

    public Collection addCaptchaGroups(Collection var1);

    public void removeCaptchaGroup(String var1);

    public boolean showCaptchaForCurrentUser();

    public void excludeNone();

    public void excludeRegisteredUsers();

    public void excludeGroups();

    public void setCaptchaGroups(Collection var1);

    public boolean isCaptchaAvailable();

    @Deprecated
    public void setImageCaptchaService(ImageCaptchaService var1);

    @Deprecated
    public ImageCaptchaService getImageCaptchaService();

    public String generateCaptchaId();
}

