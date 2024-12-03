/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.license.role.spi.RoleBasedLicenseService
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.upm.license.internal.impl.role;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.impl.OptionalService;
import com.atlassian.upm.license.role.spi.RoleBasedLicenseService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class RoleBasedLicensingServiceAccessor
extends OptionalService<RoleBasedLicenseService> {
    public RoleBasedLicensingServiceAccessor(BundleContext bundleContext) {
        super(RoleBasedLicenseService.class, bundleContext);
    }

    public Option<RoleBasedLicenseService> getRoleBasedLicenseService(ServiceReference sr) {
        return this.getService(sr);
    }

    public Option<ServiceReference> getRoleBasedLicenseServiceServiceReference() {
        return this.getServiceReference();
    }
}

