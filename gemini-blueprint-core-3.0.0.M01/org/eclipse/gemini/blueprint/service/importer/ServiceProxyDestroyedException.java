/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer;

import org.eclipse.gemini.blueprint.service.ServiceException;

public class ServiceProxyDestroyedException
extends ServiceException {
    private static final long serialVersionUID = 1773620969162174421L;

    public ServiceProxyDestroyedException() {
        super("service proxy has been destroyed");
    }

    public ServiceProxyDestroyedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceProxyDestroyedException(String message) {
        super(message);
    }

    public ServiceProxyDestroyedException(Throwable cause) {
        super(cause);
    }
}

