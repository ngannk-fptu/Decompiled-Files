/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.application.api.Application
 *  com.atlassian.application.api.ApplicationKey
 *  com.atlassian.application.api.ApplicationManager
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.core.impl;

import com.atlassian.application.api.Application;
import com.atlassian.application.api.ApplicationKey;
import com.atlassian.application.api.ApplicationManager;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import java.util.Iterator;
import java.util.function.Function;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;

public class UpmAppManagerImpl
implements UpmAppManager,
DisposableBean {
    private final Option<ServiceTracker> appMgrServiceTracker;
    private final Option<ServiceTracker> featureMgrServiceTracker;
    private static final Function<ApplicationManager, Option<UpmAppManager.ApplicationInfo>> getAppWithMostActiveUsers = new Function<ApplicationManager, Option<UpmAppManager.ApplicationInfo>>(){

        @Override
        public Option<UpmAppManager.ApplicationInfo> apply(ApplicationManager manager) {
            Option<Object> max = Option.none();
            for (Application a : manager.getApplications()) {
                if (max.isDefined()) {
                    if (((Application)max.get()).getAccess().getActiveUserCount() >= a.getAccess().getActiveUserCount()) continue;
                    max = Option.some(a);
                    continue;
                }
                max = Option.some(a);
            }
            return max.map(toAppInfo);
        }
    };
    private static final Function<Application, UpmAppManager.ApplicationInfo> toAppInfo = new Function<Application, UpmAppManager.ApplicationInfo>(){

        @Override
        public UpmAppManager.ApplicationInfo apply(Application a) {
            return new UpmAppManager.ApplicationInfo(a.getKey().value(), a.getVersion(), a.getAccess().getActiveUserCount());
        }
    };

    public UpmAppManagerImpl(BundleContext bundleContext) {
        Option<Object> st2;
        Option<Object> st1;
        try {
            Class<ApplicationManager> clazz = ApplicationManager.class;
            ServiceTracker t = new ServiceTracker(bundleContext, clazz.getName(), null);
            t.open();
            st1 = Option.some(t);
        }
        catch (NoClassDefFoundError e) {
            st1 = Option.none();
        }
        this.appMgrServiceTracker = st1;
        try {
            Class<DarkFeatureManager> clazz = DarkFeatureManager.class;
            ServiceTracker t = new ServiceTracker(bundleContext, clazz.getName(), null);
            t.open();
            st2 = Option.some(t);
        }
        catch (NoClassDefFoundError e) {
            st2 = Option.none();
        }
        this.featureMgrServiceTracker = st2;
    }

    public void destroy() {
        for (ServiceTracker t : this.appMgrServiceTracker) {
            t.close();
        }
        for (ServiceTracker t : this.featureMgrServiceTracker) {
            t.close();
        }
    }

    @Override
    public boolean isApplicationSupportEnabled() {
        return this.getAppManager().isDefined();
    }

    @Override
    public Option<UpmAppManager.ApplicationInfo> getApplication(String key) {
        if (!this.isApplicationSupportEnabled()) {
            return Option.none();
        }
        return this.getAppManager().flatMap(this.getApp(key));
    }

    @Override
    public Option<UpmAppManager.ApplicationInfo> getApplicationWithMostActiveUsers() {
        if (!this.isApplicationSupportEnabled()) {
            return Option.none();
        }
        return this.getAppManager().flatMap(getAppWithMostActiveUsers);
    }

    private Function<ApplicationManager, Option<UpmAppManager.ApplicationInfo>> getApp(final String key) {
        return new Function<ApplicationManager, Option<UpmAppManager.ApplicationInfo>>(){

            @Override
            public Option<UpmAppManager.ApplicationInfo> apply(ApplicationManager manager) {
                Iterator iterator = manager.getApplication(ApplicationKey.valueOf((String)key)).iterator();
                if (iterator.hasNext()) {
                    Application a = (Application)iterator.next();
                    return Option.some(toAppInfo.apply(a));
                }
                return Option.none();
            }
        };
    }

    @Override
    public Function<UpmAppManager.ApplicationDescriptorModuleInfo, String> applicationPluginAppKey() {
        return ap -> ap.applicationKey;
    }

    @Override
    public Function<UpmAppManager.ApplicationDescriptorModuleInfo, String> applicationPluginTypeString() {
        return ap -> ap.type.name();
    }

    @Override
    public Option<ApplicationManager> getAppManager() {
        Iterator<ServiceTracker> iterator = this.appMgrServiceTracker.iterator();
        if (iterator.hasNext()) {
            ServiceTracker t = iterator.next();
            return Option.option((ApplicationManager)t.getService());
        }
        return Option.none();
    }
}

