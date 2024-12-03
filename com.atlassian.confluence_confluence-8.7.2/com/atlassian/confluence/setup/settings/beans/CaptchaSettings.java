/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CaptchaSettings
implements Serializable {
    private static final long serialVersionUID = 1559639495910309429L;
    public static final String NONE = "none";
    public static final String REGISTERED = "registered";
    public static final String GROUPS = "groups";
    boolean enableCaptcha = false;
    boolean enableDebug = false;
    private Collection<String> captchaGroups = new ArrayList<String>();
    String exclude = "registered";

    public CaptchaSettings() {
    }

    public CaptchaSettings(CaptchaSettings captchaSettings) {
        this.enableCaptcha = captchaSettings.enableCaptcha;
        this.enableDebug = captchaSettings.enableDebug;
        this.captchaGroups = new ArrayList<String>(captchaSettings.captchaGroups);
        this.exclude = captchaSettings.exclude;
    }

    public boolean isEnableCaptcha() {
        return this.enableCaptcha;
    }

    public void setEnableCaptcha(boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public boolean isEnableDebug() {
        return this.enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public Collection<String> getCaptchaGroups() {
        return this.captchaGroups;
    }

    public void setCaptchaGroups(Collection<String> captchaGroups) {
        this.captchaGroups = captchaGroups;
    }

    public String getExclude() {
        return this.exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
}

