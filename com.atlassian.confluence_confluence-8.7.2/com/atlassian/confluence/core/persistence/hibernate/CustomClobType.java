/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.hibernate.BucketClobStringType
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.hibernate.BucketClobStringType;

public class CustomClobType
extends BucketClobStringType {
    private static final String USE_SET_STRING = "useSetClobAsString";

    public CustomClobType() {
        boolean useSetClobAsString = false;
        if (BootstrapUtils.getBootstrapManager() != null) {
            useSetClobAsString = BootstrapUtils.getBootstrapManager().getApplicationConfig().getBooleanProperty((Object)USE_SET_STRING);
        }
        this.setUseSetClobAsString(useSetClobAsString);
    }
}

