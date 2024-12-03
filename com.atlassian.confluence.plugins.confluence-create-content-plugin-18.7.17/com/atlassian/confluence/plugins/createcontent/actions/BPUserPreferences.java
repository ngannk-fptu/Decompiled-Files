/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.core.user.preferences.UserPreferences;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;

public class BPUserPreferences
extends UserPreferences {
    private final PropertySet propertySet;

    public BPUserPreferences(PropertySet propertySet) {
        super(propertySet);
        this.propertySet = propertySet;
    }

    public void setText(String key, String value) throws PropertyException {
        this.propertySet.setText(key, value);
    }

    public String getText(String key) throws PropertyException {
        return this.propertySet.getText(key);
    }
}

