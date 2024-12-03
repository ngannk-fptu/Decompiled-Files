/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.ozymandias.SafePluginPointAccess
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  com.atlassian.whisper.plugin.api.ExperienceOverride
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.licensebanner.support;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.licensebanner.support.ExperienceOverrideService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;
import com.atlassian.whisper.plugin.api.ExperienceOverride;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultExperienceOverrideService
implements ExperienceOverrideService {
    private final LocaleManager localeManager;
    private final UserManager userManager;
    private final UserAccessor userAccessor;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public DefaultExperienceOverrideService(@ComponentImport LocaleManager localeManager, @ComponentImport UserAccessor userAccessor, @ComponentImport UserManager userManager, @ComponentImport PluginAccessor pluginAccessor) {
        this.localeManager = localeManager;
        this.userManager = userManager;
        this.userAccessor = userAccessor;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public boolean isOverridden(UserKey userKey, final String experienceId) {
        final UserProfile userProfile = this.userManager.getUserProfile(userKey);
        ConfluenceUser userByKey = this.userAccessor.getUserByKey(userKey);
        final Locale locale = this.localeManager.getLocale((User)userByKey);
        return (Boolean)SafePluginPointAccess.call((Callable)new Callable<Boolean>(){

            @Override
            public Boolean call() {
                List experienceOverrides = DefaultExperienceOverrideService.this.pluginAccessor.getEnabledModulesByClass(ExperienceOverride.class);
                return experienceOverrides.stream().anyMatch(experienceOverride -> experienceOverride.hasOverride(userProfile, experienceId, locale));
            }
        }).getOrElse((Object)false);
    }
}

