/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceException
 */
package org.osgi.service.blueprint.container;

import org.osgi.framework.ServiceException;

public class ServiceUnavailableException
extends ServiceException {
    private static final long serialVersionUID = 1L;
    private final String filter;

    public ServiceUnavailableException(String message, String filter) {
        super(message, 1);
        this.filter = filter;
    }

    public ServiceUnavailableException(String message, String filter, Throwable cause) {
        super(message, 1, cause);
        this.filter = filter;
    }

    public String getFilter() {
        return this.filter;
    }
}

