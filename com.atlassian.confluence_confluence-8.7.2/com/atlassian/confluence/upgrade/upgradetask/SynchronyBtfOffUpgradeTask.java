/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;

public class SynchronyBtfOffUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final ClusterManager clusterManager;
    private final ApplicationConfiguration applicationConfiguration;
    private final DarkFeaturesManager darkFeaturesManager;
    private final BandanaManager bandanaManager;

    public SynchronyBtfOffUpgradeTask(ClusterManager clusterManager, ApplicationConfiguration applicationConfiguration, DarkFeaturesManager darkFeaturesManager, BandanaManager bandanaManager) {
        this.clusterManager = clusterManager;
        this.applicationConfiguration = applicationConfiguration;
        this.darkFeaturesManager = darkFeaturesManager;
        this.bandanaManager = bandanaManager;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return "7901";
    }

    public String getShortDescription() {
        return "Upgrade \"synchrony.btf.off\" property";
    }

    public void doUpgrade() throws Exception {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext();
        if (!this.clusterManager.isClustered()) {
            this.bandanaManager.setValue((BandanaContext)context, "synchrony.btf.off", (Object)this.applicationConfiguration.getBooleanProperty((Object)"synchrony.btf.off"));
        } else {
            DarkFeatures siteDarkFeatures = this.darkFeaturesManager.getSiteDarkFeatures();
            boolean isOff = !siteDarkFeatures.isFeatureEnabled("site-wide.shared-drafts") && !siteDarkFeatures.isFeatureEnabled("site-wide.synchrony");
            this.bandanaManager.setValue((BandanaContext)context, "synchrony.btf.off", (Object)isOff);
        }
    }
}

