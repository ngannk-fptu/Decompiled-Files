/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.exception;

import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.ServiceUnavailableException;

public abstract class BlueprintExceptionFactory {
    public static RuntimeException createServiceUnavailableException(Filter filter) {
        return BlueprintFactory.createServiceUnavailableException(filter);
    }

    public static RuntimeException createServiceUnavailableException(ServiceReference reference) {
        return BlueprintFactory.createServiceUnavailableException(reference);
    }

    private static abstract class BlueprintFactory {
        private BlueprintFactory() {
        }

        private static RuntimeException createServiceUnavailableException(Filter filter) {
            return new ServiceUnavailableException("service matching filter=[" + filter + "] unavailable", filter.toString());
        }

        private static RuntimeException createServiceUnavailableException(ServiceReference reference) {
            String id = reference == null ? "null" : "" + OsgiServiceReferenceUtils.getServiceId(reference);
            return new ServiceUnavailableException("service with id=[" + id + "] unavailable", OsgiFilterUtils.getFilter(reference));
        }
    }
}

