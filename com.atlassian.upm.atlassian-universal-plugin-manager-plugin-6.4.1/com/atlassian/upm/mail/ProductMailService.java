/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.UpmEmail;

public interface ProductMailService {
    public boolean isConfigured();

    public boolean isDisabled();

    public void sendMail(UpmEmail var1);

    public UpmEmail.Format getUserEmailFormatPreference(UserKey var1);

    public Option<String> getInstanceName();
}

