/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.Target;

public interface ReferenceListener {
    public Target getListenerComponent();

    public String getBindMethod();

    public String getUnbindMethod();
}

