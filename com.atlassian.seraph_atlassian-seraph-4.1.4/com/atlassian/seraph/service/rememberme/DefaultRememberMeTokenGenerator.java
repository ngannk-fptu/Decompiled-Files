/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.seraph.service.rememberme.DefaultRememberMeToken;
import com.atlassian.seraph.service.rememberme.RememberMeToken;
import com.atlassian.seraph.service.rememberme.RememberMeTokenGenerator;

public class DefaultRememberMeTokenGenerator
implements RememberMeTokenGenerator {
    @Override
    public RememberMeToken generateToken(String userName) {
        String base64 = DefaultSecureTokenGenerator.getInstance().generateToken();
        return DefaultRememberMeToken.builder(base64).setUserName(userName).setCreatedTime(System.currentTimeMillis()).build();
    }
}

