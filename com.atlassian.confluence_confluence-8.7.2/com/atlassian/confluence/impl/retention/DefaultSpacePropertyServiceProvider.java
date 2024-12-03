/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.content.SpacePropertyService
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.impl.retention.SpacePropertyServiceProvider;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.util.concurrent.LazyReference;
import java.util.stream.Stream;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class DefaultSpacePropertyServiceProvider
implements SpacePropertyServiceProvider {
    private final LazyReference<ServiceTracker> serviceTrackerRef;

    public DefaultSpacePropertyServiceProvider(final OsgiContainerManager osgiContainerManager) {
        this.serviceTrackerRef = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                return osgiContainerManager.getServiceTracker(SpacePropertyService.class.getName());
            }
        };
    }

    @Override
    public SpacePropertyService get() {
        ServiceTracker serviceTracker = (ServiceTracker)this.serviceTrackerRef.get();
        if (serviceTracker == null) {
            throw new IllegalStateException("osgi service tracker must not be null");
        }
        ServiceReference[] serviceReferences = serviceTracker.getServiceReferences();
        return Stream.of(serviceReferences).map(arg_0 -> ((ServiceTracker)serviceTracker).getService(arg_0)).filter(spacePropertyService -> spacePropertyService instanceof SpacePropertyService).map(spacePropertyService -> (SpacePropertyService)spacePropertyService).findFirst().orElseThrow(() -> new IllegalStateException("osgi service SpacePropertyService doesn't exist"));
    }
}

