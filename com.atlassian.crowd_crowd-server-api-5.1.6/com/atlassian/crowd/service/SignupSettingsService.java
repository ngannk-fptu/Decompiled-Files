/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service;

import java.util.List;

public interface SignupSettingsService {
    public boolean isEmailSentOnSignUp();

    public void setEmailSentOnSignUp(boolean var1);

    public void setSignupEnabled(boolean var1);

    public boolean isSignupEnabled();

    public void setRestrictedDomains(List<String> var1);

    public List<String> getRestrictedDomains();

    public boolean isEmailAllowed(String var1);
}

