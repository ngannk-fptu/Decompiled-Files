/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.RegistrationProvider
 */
package com.atlassian.mywork.host.provider;

import com.atlassian.mywork.service.RegistrationProvider;

public class MyWorkRegistrationProvider
implements RegistrationProvider {
    public String getApplication() {
        return this.getPackage();
    }

    public String getPackage() {
        return this.getClass().getPackage().getName();
    }

    public String getPluginId() {
        return "com.atlassian.mywork.mywork-confluence-host-plugin";
    }
}

