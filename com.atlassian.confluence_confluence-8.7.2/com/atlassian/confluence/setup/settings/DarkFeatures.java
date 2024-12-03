/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class DarkFeatures {
    private final Set<String> systemEnabledFeatures;
    private final Set<String> siteEnabledFeatures;
    private final Set<String> userEnabledFeatures;

    public DarkFeatures(Set<String> systemEnabledFeatures, Set<String> siteEnabledFeatures, Set<String> userEnabledFeatures) {
        this.systemEnabledFeatures = ImmutableSet.copyOf(systemEnabledFeatures);
        this.userEnabledFeatures = ImmutableSet.copyOf(userEnabledFeatures);
        this.siteEnabledFeatures = ImmutableSet.copyOf(siteEnabledFeatures);
    }

    public Set<String> getSystemEnabledFeatures() {
        return this.systemEnabledFeatures;
    }

    public Set<String> getSiteEnabledFeatures() {
        return this.siteEnabledFeatures;
    }

    public Set<String> getUserEnabledFeatures() {
        return this.userEnabledFeatures;
    }

    public Set<String> getGlobalEnabledFeatures() {
        return Sets.union(this.systemEnabledFeatures, this.siteEnabledFeatures);
    }

    public Set<String> getAllEnabledFeatures() {
        return Sets.union((Set)Sets.union(this.userEnabledFeatures, this.siteEnabledFeatures), this.systemEnabledFeatures);
    }

    public String getAllEnabledFeaturesAsString() {
        return StringUtils.join(this.getAllEnabledFeatures(), (String)",");
    }

    public boolean isFeatureEnabled(String featureKey) {
        return this.getAllEnabledFeatures().contains(StringUtils.trim((String)featureKey)) && !this.getAllEnabledFeatures().contains(StringUtils.trim((String)featureKey) + ".disable");
    }

    public static boolean isDarkFeatureEnabled(String featureKey) {
        DarkFeatures darkFeatures = DarkFeatures.getDarkFeaturesManager().getDarkFeatures();
        return darkFeatures.isFeatureEnabled(featureKey);
    }

    public static boolean isDarkFeatureEnabled(ConfluenceUser user, String featureKey) {
        DarkFeatures darkFeatures = DarkFeatures.getDarkFeaturesManager().getDarkFeatures(user);
        return darkFeatures.isFeatureEnabled(featureKey);
    }

    @Deprecated
    public static boolean isDarkFeatureEnabled(User user, String featureKey) {
        return DarkFeatures.isDarkFeatureEnabled(FindUserHelper.getUser(user), featureKey);
    }

    private static DarkFeaturesManager getDarkFeaturesManager() {
        return (DarkFeaturesManager)ContainerManager.getComponent((String)"darkFeaturesManager");
    }
}

