/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradedFlag
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradedFlag;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultCollaborativeEditingHelper
implements CollaborativeEditingHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultCollaborativeEditingHelper.class);
    private final Supplier<DarkFeaturesManager> darkFeaturesManagerSupplier = () -> darkFeaturesManager;
    private final UpgradedFlag upgradedFlag;

    public DefaultCollaborativeEditingHelper(DarkFeaturesManager darkFeaturesManager, UpgradedFlag upgradedFlag) {
        this.upgradedFlag = upgradedFlag;
    }

    public DefaultCollaborativeEditingHelper(DarkFeaturesManager darkFeaturesManager, UpgradeManager upgradeManager) {
        this(darkFeaturesManager, () -> ((UpgradeManager)upgradeManager).isUpgraded());
    }

    @Override
    public boolean isSharedDraftsFeatureEnabled(String spaceKey) {
        return this.isFeatureEnabled("shared-drafts", spaceKey);
    }

    @Override
    public boolean isUpgraded() {
        return this.upgradedFlag.isUpgraded();
    }

    @Override
    @Deprecated
    public boolean isLimitedModeEnabled(String spaceKey) {
        return false;
    }

    @Override
    public String getEditMode(String spaceKey) {
        return this.isSharedDraftsFeatureEnabled(spaceKey) ? "collaborative" : "legacy";
    }

    public static String getSpaceDarkFeature(String spaceKey) {
        return "shared-drafts." + StringUtils.upperCase((String)spaceKey);
    }

    @Deprecated
    public static String getSynchronySpaceDarkFeature(String spaceKey) {
        return "synchrony." + StringUtils.upperCase((String)spaceKey);
    }

    @Override
    public boolean isOverLimit(int numberOfConcurrentUsers) {
        return this.isUserLimitEnabled() && numberOfConcurrentUsers > this.getUserLimit();
    }

    @Override
    public int getUserLimit() {
        return Integer.getInteger("confluence.collab.edit.user.limit", 12);
    }

    private boolean isFeatureEnabled(String darkFeatureKey, String spaceKey) {
        try {
            DarkFeatures siteDarkFeatures = this.darkFeaturesManagerSupplier.get().getSiteDarkFeatures();
            boolean isEnabledOnSite = siteDarkFeatures.isFeatureEnabled("site-wide." + darkFeatureKey);
            if (isEnabledOnSite || StringUtils.isEmpty((CharSequence)spaceKey)) {
                return isEnabledOnSite;
            }
            return siteDarkFeatures.isFeatureEnabled(darkFeatureKey + "." + StringUtils.upperCase((String)spaceKey));
        }
        catch (Exception e) {
            log.error("Exception checking dark feature " + darkFeatureKey + " : " + e.getMessage());
            log.debug("Exception checking dark feature " + darkFeatureKey + " : ", (Throwable)e);
            return false;
        }
    }

    private boolean isUserLimitEnabled() {
        try {
            return !this.darkFeaturesManagerSupplier.get().getDarkFeatures().isFeatureEnabled("confluence.collab.edit.user.limit.disable");
        }
        catch (Exception e) {
            log.error("Exception checking user limit dark feature : " + e.getMessage());
            log.debug("Exception checking user limit dark feature : ", (Throwable)e);
            return false;
        }
    }
}

