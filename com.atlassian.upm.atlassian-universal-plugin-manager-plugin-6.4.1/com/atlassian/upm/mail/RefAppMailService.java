/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;

public class RefAppMailService
implements ProductMailService {
    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return !this.isConfigured();
    }

    @Override
    public void sendMail(UpmEmail upmEmail) {
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        return UpmEmail.Format.TEXT;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.some("RefImpl App");
    }
}

