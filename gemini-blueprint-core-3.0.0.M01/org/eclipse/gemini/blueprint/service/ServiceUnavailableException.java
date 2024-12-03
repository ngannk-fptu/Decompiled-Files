/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service;

import org.eclipse.gemini.blueprint.service.ServiceException;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class ServiceUnavailableException
extends ServiceException {
    private static final long serialVersionUID = -3479837278220329490L;

    public ServiceUnavailableException(Filter filter) {
        super("service matching filter=[" + filter + "] unavailable");
    }

    public ServiceUnavailableException(String filter) {
        super("service matching filter=[" + filter + "] unavailable");
    }

    public ServiceUnavailableException(ServiceReference reference) {
        super("service with id=[" + (reference == null ? "null" : "" + OsgiServiceReferenceUtils.getServiceId(reference)) + "] unavailable");
    }
}

