/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.security.core.Authentication
 */
package com.atlassian.crowd.service;

import com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface CrowdRememberMeService {
    public Optional<Authentication> authenticate(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2);

    public void removeCookie(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2);

    public void createCookie(@Nonnull Authentication var1, @Nonnull HttpServletRequest var2, @Nonnull HttpServletResponse var3);

    public void clearAllTokensForSeries(String var1);

    public void clearAllTokensForUserInDirectory(String var1, Long var2);

    public void clearAllTokens();

    public void clearAllExpiredTokens();

    public void clearAllTokensForDirectory(Long var1);

    public void saveConfiguration(CrowdSpecificRememberMeSettings var1);

    public CrowdSpecificRememberMeSettings getConfiguration();
}

