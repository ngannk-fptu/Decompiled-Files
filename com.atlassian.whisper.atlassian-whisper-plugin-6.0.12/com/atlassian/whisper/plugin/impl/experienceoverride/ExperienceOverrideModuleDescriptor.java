/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.ExperienceOverride
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl.experienceoverride;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.ExperienceOverride;
import com.atlassian.whisper.plugin.api.MessagesService;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ExperienceOverrideModuleDescriptor
extends AbstractModuleDescriptor<ExperienceOverride> {
    private final ExperienceOverride experienceOverride;

    @Inject
    public ExperienceOverrideModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, final MessagesService messagesService) {
        super(moduleFactory);
        this.experienceOverride = new ExperienceOverride(){

            public boolean hasOverride(UserProfile user, String experienceId, Locale locale) {
                return messagesService.hasOverride(user, experienceId, locale);
            }

            public boolean hasGlobalOverride(String experienceId) {
                return messagesService.hasGlobalOverride(experienceId);
            }
        };
    }

    public ExperienceOverride getModule() {
        return this.experienceOverride;
    }

    public Class<ExperienceOverride> getModuleClass() {
        return ExperienceOverride.class;
    }
}

