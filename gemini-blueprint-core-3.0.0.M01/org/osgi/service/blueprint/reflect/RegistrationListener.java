/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.Target;

public interface RegistrationListener {
    public Target getListenerComponent();

    public String getRegistrationMethod();

    public String getUnregistrationMethod();
}

