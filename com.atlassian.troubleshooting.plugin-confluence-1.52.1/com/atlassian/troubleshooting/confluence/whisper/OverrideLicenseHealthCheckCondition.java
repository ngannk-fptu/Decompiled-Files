/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.whisper.plugin.api.ExperienceOverride
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.whisper;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import com.atlassian.whisper.plugin.api.ExperienceOverride;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OverrideLicenseHealthCheckCondition
implements SupportHealthCheckCondition {
    @VisibleForTesting
    static final String EXPERIENCE_ID = "license-health-check";
    private static final Logger LOGGER = LoggerFactory.getLogger(OverrideLicenseHealthCheckCondition.class);
    private final ExperienceOverrideSafeCheck experienceOverrideSafeCheck;

    @Autowired
    public OverrideLicenseHealthCheckCondition(PluginAccessor pluginAccessor) {
        this.experienceOverrideSafeCheck = new ExperienceOverrideSafeCheck(pluginAccessor);
    }

    @Override
    public boolean shouldDisplay() {
        boolean shouldDisplay = this.experienceOverrideSafeCheck.shouldDisplay();
        LOGGER.debug("LicenseHealthCheck should be displayed: {}", (Object)shouldDisplay);
        return shouldDisplay;
    }

    private static class ExperienceOverrideSafeCheck {
        private final PluginAccessor pluginAccessor;

        private ExperienceOverrideSafeCheck(PluginAccessor pluginAccessor) {
            this.pluginAccessor = pluginAccessor;
        }

        private boolean shouldDisplay() {
            try {
                Collection experienceOverrides = this.pluginAccessor.getModules(moduleDescriptor -> ExperienceOverride.class.equals((Object)moduleDescriptor.getModuleClass()));
                return experienceOverrides.stream().noneMatch(experienceOverride -> experienceOverride.hasGlobalOverride(OverrideLicenseHealthCheckCondition.EXPERIENCE_ID));
            }
            catch (Exception | LinkageError exception) {
                LOGGER.debug("Failed to check experience overrides because of {}: {}", (Object)exception.getClass().getName(), (Object)exception.getMessage());
                return true;
            }
        }
    }
}

