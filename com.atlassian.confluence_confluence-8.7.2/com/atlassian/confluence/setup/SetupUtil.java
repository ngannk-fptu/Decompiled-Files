/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.util.GeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SetupUtil {
    private static Logger log = LoggerFactory.getLogger(SetupUtil.class);
    public static final String SETUP_SELECTED_BUNDLE_PLUGINS = "setup.selectedBundlePluginKeys";

    public static String getResultTypeForSetupAction() {
        return GeneralUtil.isSetupComplete() ? "adminsuccess" : "success";
    }
}

