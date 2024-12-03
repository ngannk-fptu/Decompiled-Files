/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@AdminOnly
public class SiteDarkFeaturesAction
extends ConfluenceActionSupport {
    private String featureKey;
    private DarkFeaturesManager darkFeaturesManager;
    private DarkFeatures darkFeatures;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.featureKey)) {
            this.darkFeaturesManager.enableSiteFeature(this.featureKey.trim());
        }
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doRemove() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.featureKey)) {
            this.darkFeaturesManager.disableSiteFeature(this.featureKey.trim());
        }
        return "success";
    }

    public List<String> getSystemEnabledFeatures() {
        ArrayList enabledFeatures = Lists.newArrayList(this.getDarkFeatures().getSystemEnabledFeatures());
        Collections.sort(enabledFeatures);
        return enabledFeatures;
    }

    public List<String> getSiteEnabledFeatures() {
        ArrayList enabledFeatures = Lists.newArrayList(this.getDarkFeatures().getSiteEnabledFeatures());
        Collections.sort(enabledFeatures);
        return enabledFeatures;
    }

    private DarkFeatures getDarkFeatures() {
        if (this.darkFeatures == null) {
            this.darkFeatures = this.darkFeaturesManager.getDarkFeatures();
        }
        return this.darkFeatures;
    }

    public void setFeatureKey(String featureKey) {
        this.featureKey = featureKey;
    }

    public void setDarkFeaturesManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }
}

