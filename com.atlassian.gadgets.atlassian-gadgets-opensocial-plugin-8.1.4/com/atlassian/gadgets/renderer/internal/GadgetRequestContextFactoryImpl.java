/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetRequestContext$Builder
 *  com.atlassian.gadgets.GadgetRequestContext$User
 *  com.atlassian.gadgets.GadgetRequestContextFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.BooleanUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetRequestContextFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Locale;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public final class GadgetRequestContextFactoryImpl
implements GadgetRequestContextFactory {
    static final String IGNORE_CACHE_PROPERTY_KEY = "com.atlassian.gadgets.dashboard.ignoreCache";
    static final String DEBUG_PROPERTY_KEY = "com.atlassian.gadgets.debug";
    private final LocaleResolver localeResolver;
    private final UserManager userManager;

    @Autowired
    public GadgetRequestContextFactoryImpl(@ComponentImport LocaleResolver localeResolver, @ComponentImport UserManager userManager) {
        this.localeResolver = localeResolver;
        this.userManager = userManager;
    }

    public GadgetRequestContext get(HttpServletRequest request) {
        return this.getBuilder(request).build();
    }

    public GadgetRequestContext.Builder getBuilder(HttpServletRequest request) {
        Locale locale = this.localeResolver.getLocale(request);
        Optional<UserProfile> userProfile = Optional.ofNullable(this.userManager.getRemoteUser(request));
        GadgetRequestContext.User user = userProfile.map(profile -> new GadgetRequestContext.User(profile.getUserKey().getStringValue(), profile.getUsername())).orElse(null);
        return GadgetRequestContext.Builder.gadgetRequestContext().locale(locale).ignoreCache(this.getCacheSetting(request)).debug(this.isDebugEnabled(request)).user(user);
    }

    private boolean getCacheSetting(HttpServletRequest request) {
        return this.isEnabled(request, "ignoreCache", IGNORE_CACHE_PROPERTY_KEY, false);
    }

    private boolean isDebugEnabled(HttpServletRequest request) {
        return this.isEnabled(request, "debug", DEBUG_PROPERTY_KEY, false);
    }

    private boolean isEnabled(HttpServletRequest request, String parameterName, String propertyName, boolean defaultValue) {
        Boolean enabled = BooleanUtils.toBooleanObject((String)request.getParameter(parameterName));
        if (enabled != null) {
            return enabled;
        }
        enabled = BooleanUtils.toBooleanObject((String)System.getProperty(propertyName));
        if (enabled != null) {
            return enabled;
        }
        return defaultValue;
    }
}

