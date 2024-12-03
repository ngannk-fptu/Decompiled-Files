/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.core.ContentEntityObject;

public class GlobalDescription
extends ContentEntityObject {
    public static final String GLOBAL_DESCRIPTION = "globaldescription";

    @Override
    public String getNameForComparison() {
        return GLOBAL_DESCRIPTION;
    }

    @Override
    public String getType() {
        return GLOBAL_DESCRIPTION;
    }

    @Override
    public String getUrlPath() {
        return "/";
    }

    @Override
    public String getDisplayTitle() {
        return "";
    }
}

