/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.model.sso;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.sso.BaseApplicationSamlConfiguration;

public interface ApplicationSamlConfiguration
extends BaseApplicationSamlConfiguration {
    public Application getApplication();

    public boolean isEnabled();
}

