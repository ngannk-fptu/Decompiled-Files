/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.SupportZipBundleAccessor;
import java.util.Arrays;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class OsgiSupportZipBundleAccessor
implements SupportZipBundleAccessor,
DisposableBean {
    private final ServiceTracker applicationInfoBundleTracker;

    @Autowired
    public OsgiSupportZipBundleAccessor(BundleContext bundleContext) {
        this.applicationInfoBundleTracker = new ServiceTracker(bundleContext, SupportZipBundle.class.getName(), null);
        this.applicationInfoBundleTracker.open();
    }

    @Override
    public List<SupportZipBundle> getBundles() {
        return Arrays.asList(this.applicationInfoBundleTracker.getServices());
    }

    public void destroy() throws Exception {
        this.applicationInfoBundleTracker.close();
    }
}

