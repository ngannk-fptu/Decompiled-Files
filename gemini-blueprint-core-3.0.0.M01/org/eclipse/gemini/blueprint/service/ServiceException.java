/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service;

import org.eclipse.gemini.blueprint.OsgiException;

public class ServiceException
extends OsgiException {
    private static final long serialVersionUID = 8290043693193600721L;

    public ServiceException() {
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}

