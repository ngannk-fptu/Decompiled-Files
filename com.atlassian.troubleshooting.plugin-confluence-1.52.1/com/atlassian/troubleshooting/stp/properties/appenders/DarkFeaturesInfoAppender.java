/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.features.EnabledDarkFeatures
 *  com.atlassian.sal.api.features.FeatureKeyScope
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.features.FeatureKeyScope;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;

public class DarkFeaturesInfoAppender
extends RootLevelSupportDataAppender {
    private static final String DARK_FEATURE_KEY = "stp.properties.application.dark.features.key";
    private static final String DARK_FEATURES_HEADER_CATEGORY = "stp.properties.application.dark.features.enabled.information";
    private static final String DARK_FEATURE_CATEGORY = "stp.properties.application.dark.features.dark.feature";
    private final DarkFeatureManager darkFeatureManager;

    @Autowired
    public DarkFeaturesInfoAppender(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    @Override
    public void addSupportData(SupportDataBuilder supportBuilder) {
        SupportDataBuilder categorizedBuilder = supportBuilder.addCategory(DARK_FEATURES_HEADER_CATEGORY);
        this.addSystemDarkFeatures(categorizedBuilder);
        this.addSiteDarkFeatures(categorizedBuilder);
    }

    private Set<String> getSystemDarkFeatures() {
        EnabledDarkFeatures featuresEnabledForAllUsers = this.darkFeatureManager.getFeaturesEnabledForAllUsers();
        return new TreeSet<String>((Collection<String>)featuresEnabledForAllUsers.getFeatureKeys(featureKeyScope -> featureKeyScope.equals((Object)FeatureKeyScope.ALL_USERS_READ_ONLY)));
    }

    private Set<String> getSiteDarkFeatures() {
        EnabledDarkFeatures featuresEnabledForAllUsers = this.darkFeatureManager.getFeaturesEnabledForAllUsers();
        return new TreeSet<String>((Collection<String>)featuresEnabledForAllUsers.getFeatureKeys(featureKeyScope -> featureKeyScope.equals((Object)FeatureKeyScope.ALL_USERS)));
    }

    private void addSystemDarkFeatures(SupportDataBuilder builder) {
        SupportDataBuilder keyDarkFeatureSystem = builder.addCategory("stp.properties.application.dark.features.system.enabled");
        for (String feature : this.getSystemDarkFeatures()) {
            SupportDataBuilder darkFeatureBuilder = keyDarkFeatureSystem.addCategory(DARK_FEATURE_CATEGORY);
            darkFeatureBuilder.addValue(DARK_FEATURE_KEY, feature);
        }
    }

    private void addSiteDarkFeatures(SupportDataBuilder builder) {
        SupportDataBuilder keyDarkFeatureSite = builder.addCategory("stp.properties.application.dark.features.site.enabled");
        for (String feature : this.getSiteDarkFeatures()) {
            SupportDataBuilder darkFeatureBuilder = keyDarkFeatureSite.addCategory(DARK_FEATURE_CATEGORY);
            darkFeatureBuilder.addValue(DARK_FEATURE_KEY, feature);
        }
    }
}

