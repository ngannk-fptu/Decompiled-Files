/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.application.api.ApplicationManager
 */
package com.atlassian.upm.core.impl;

import com.atlassian.application.api.ApplicationManager;
import com.atlassian.upm.api.util.Option;
import java.util.Objects;
import java.util.function.Function;

public interface UpmAppManager {
    public boolean isApplicationSupportEnabled();

    public Option<ApplicationInfo> getApplication(String var1);

    public Option<ApplicationInfo> getApplicationWithMostActiveUsers();

    public Function<ApplicationDescriptorModuleInfo, String> applicationPluginAppKey();

    public Function<ApplicationDescriptorModuleInfo, String> applicationPluginTypeString();

    public Option<ApplicationManager> getAppManager();

    public static enum ApplicationPluginType {
        PRIMARY,
        APPLICATION,
        UTILITY;

    }

    public static class ApplicationDescriptorModuleInfo {
        public final String applicationKey;
        public final ApplicationPluginType type;

        ApplicationDescriptorModuleInfo(String applicationKey, ApplicationPluginType type) {
            this.applicationKey = Objects.requireNonNull(applicationKey, "applicationKey");
            this.type = Objects.requireNonNull(type, "type");
        }
    }

    public static class ApplicationInfo {
        public final String key;
        public final String version;
        public final int numberOfActiveUsers;

        ApplicationInfo(String key, String version, int n) {
            this.key = Objects.requireNonNull(key, "key");
            this.version = Objects.requireNonNull(version);
            this.numberOfActiveUsers = n;
        }
    }
}

