/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.data.activeobjects.context;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.spel.spi.EvaluationContextExtension;

public class SalUserProfileContextExtension
implements EvaluationContextExtension {
    private static final Logger logger = LoggerFactory.getLogger(SalUserProfileContextExtension.class);
    private final UserManager userManager;

    public SalUserProfileContextExtension(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public String getExtensionId() {
        return "salcontext";
    }

    @Override
    public Map<String, Object> getProperties() {
        UserProfile userProfile = this.userManager.getRemoteUser();
        logger.debug("Got userprofile from SalUserContextExtension: [{}]", (Object)userProfile);
        return Collections.singletonMap("salUserProfile", userProfile);
    }
}

