/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.google.common.base.Predicate;

public enum CustomAppPredicates implements Predicate<CustomApp>
{
    hasNoSourceApplicationUrl{

        public boolean apply(CustomApp app) {
            return app.getSourceApplicationUrl() == null;
        }
    };

}

