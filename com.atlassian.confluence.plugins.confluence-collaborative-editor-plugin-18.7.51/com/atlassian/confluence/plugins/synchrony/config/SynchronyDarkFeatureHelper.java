/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.config;

import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-dark-feature-helper")
public class SynchronyDarkFeatureHelper {
    public static final String SHARED_DRAFTS_DARK_FEATURE = "shared-drafts";
    public static final String SITE_WIDE_SHARED_DRAFTS_DARK_FEATURE = "site-wide.shared-drafts";
    public static final String SITE_WIDE_SHARED_DRAFTS_DARK_FEATURE_DISABLE = "site-wide.shared-drafts.disable";
    public static final String SITE_WIDE_SYNCHRONY_PROD_OVERRIDE = "site-wide.synchrony-prod-override";
    private static final Logger log = LoggerFactory.getLogger(SynchronyDarkFeatureHelper.class);
    private final DraftsTransitionHelper draftsTransitionHelper;

    @Autowired
    public SynchronyDarkFeatureHelper(@ComponentImport DraftsTransitionHelper draftsTransitionHelper) {
        this.draftsTransitionHelper = draftsTransitionHelper;
    }

    public boolean isSynchronyFeatureEnabled(String spaceKey) {
        try {
            return this.draftsTransitionHelper.isSharedDraftsFeatureEnabled(spaceKey);
        }
        catch (Exception e) {
            log.debug("Exception checking Synchrony feature status: " + e.getMessage());
            return false;
        }
    }
}

