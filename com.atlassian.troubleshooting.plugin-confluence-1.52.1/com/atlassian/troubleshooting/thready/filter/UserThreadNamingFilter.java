/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.thready.filter;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.troubleshooting.thready.filter.AbstractThreadNamingFilter;
import com.atlassian.troubleshooting.thready.manager.RequestValidator;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.atlassian.troubleshooting.thready.manager.ThreadNameManager;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class UserThreadNamingFilter
extends AbstractThreadNamingFilter {
    private final UserManager userManager;

    public UserThreadNamingFilter(ThreadNameManager threadNameManager, ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager, RequestValidator requestValidator, UserManager userManager) {
        super(threadNameManager, threadDiagnosticsConfigurationManager, requestValidator);
        this.userManager = userManager;
    }

    @Override
    protected void updateAttributes(HttpServletRequest request, ThreadNameManager threadNameManager) {
        this.getUserName().ifPresent(u -> threadNameManager.putThreadAttribute("user", (String)u));
    }

    private Optional<String> getUserName() {
        String userName;
        UserProfile user = this.userManager.getRemoteUser();
        if (user != null && !StringUtils.isEmpty((CharSequence)(userName = user.getUsername()))) {
            return Optional.of(userName);
        }
        return Optional.empty();
    }
}

