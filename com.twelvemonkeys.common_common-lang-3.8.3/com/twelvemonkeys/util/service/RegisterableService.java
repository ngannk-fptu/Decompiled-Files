/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.service;

import com.twelvemonkeys.util.service.ServiceRegistry;

public interface RegisterableService {
    public void onRegistration(ServiceRegistry var1, Class var2);

    public void onDeregistration(ServiceRegistry var1, Class var2);
}

