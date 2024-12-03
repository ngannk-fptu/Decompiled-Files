/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.RegistrationProvider
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.mywork.service.RegistrationProvider;

public class ConfluenceRegistrationProvider
implements RegistrationProvider {
    public String getApplication() {
        return this.getPackage();
    }

    public String getPackage() {
        return this.getClass().getPackage().getName();
    }

    public String getPluginId() {
        return "com.atlassian.mywork.mywork-confluence-provider-plugin";
    }
}

