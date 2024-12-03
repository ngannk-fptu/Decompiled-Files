/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UserDarkFeaturesAction
extends AbstractUserProfileAction {
    private String featureKey;
    private DarkFeaturesManager darkFeaturesManager;
    private DarkFeatures darkFeatures;

    @Override
    public String doDefault() throws Exception {
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.featureKey)) {
            this.darkFeaturesManager.enableUserFeature(this.featureKey.trim());
        }
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doRemove() throws Exception {
        if (StringUtils.isNotBlank((CharSequence)this.featureKey)) {
            this.darkFeaturesManager.disableUserFeature(this.featureKey.trim());
        }
        return "success";
    }

    public List<String> getGlobalEnabledFeatures() {
        ArrayList enabledFeatures = Lists.newArrayList(this.getDarkFeatures().getGlobalEnabledFeatures());
        Collections.sort(enabledFeatures);
        return enabledFeatures;
    }

    public List<String> getUserEnabledFeatures() {
        ArrayList enabledFeatures = Lists.newArrayList(this.getDarkFeatures().getUserEnabledFeatures());
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

    public String getFeatureKey() {
        return this.featureKey;
    }

    public void setDarkFeaturesManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }
}

