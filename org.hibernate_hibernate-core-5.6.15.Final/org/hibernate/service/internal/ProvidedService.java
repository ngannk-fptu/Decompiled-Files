/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.internal;

public class ProvidedService<R> {
    private final Class<R> serviceRole;
    private final R service;

    public ProvidedService(Class<R> serviceRole, R service) {
        this.serviceRole = serviceRole;
        this.service = service;
    }

    public Class<R> getServiceRole() {
        return this.serviceRole;
    }

    public R getService() {
        return this.service;
    }
}

