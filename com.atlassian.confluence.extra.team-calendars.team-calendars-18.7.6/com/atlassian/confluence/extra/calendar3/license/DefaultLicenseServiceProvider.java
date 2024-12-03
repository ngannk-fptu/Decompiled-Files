/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.confluence.extra.calendar3.license.LicenseServiceProvider;
import com.atlassian.confluence.license.LicenseService;
import java.util.Optional;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultLicenseServiceProvider
implements LicenseServiceProvider,
DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(DefaultLicenseServiceProvider.class);
    private BundleContext bundleContext;
    private Optional<LicenseService> licenseService;
    private ServiceTracker licenseServiceServiceTracker;

    @Autowired
    public DefaultLicenseServiceProvider(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.initOsgiServiceTracker();
    }

    @Override
    @Nullable
    public LicenseService getLicenseService() {
        return this.licenseService.orElse(null);
    }

    public void destroy() throws Exception {
        this.licenseServiceServiceTracker.close();
    }

    private void initOsgiServiceTracker() {
        this.licenseServiceServiceTracker = new ServiceTracker(this.bundleContext, "com.atlassian.confluence.license.LicenseService", new ServiceTrackerCustomizer(){

            public Object addingService(ServiceReference serviceReference) {
                logger.info("TC listen on LicenseService adding");
                Object serviceObj = DefaultLicenseServiceProvider.this.bundleContext.getService(serviceReference);
                DefaultLicenseServiceProvider.this.licenseService = Optional.ofNullable((LicenseService)serviceObj);
                return serviceObj;
            }

            public void modifiedService(ServiceReference serviceReference, Object o) {
                logger.info("TC listen on LicenseService modified");
                Object serviceObj = DefaultLicenseServiceProvider.this.bundleContext.getService(serviceReference);
                DefaultLicenseServiceProvider.this.licenseService = Optional.ofNullable((LicenseService)serviceObj);
            }

            public void removedService(ServiceReference serviceReference, Object o) {
                logger.info("TC listen on LicenseService removed");
                DefaultLicenseServiceProvider.this.licenseService = Optional.empty();
            }
        });
        this.licenseServiceServiceTracker.open();
    }
}

