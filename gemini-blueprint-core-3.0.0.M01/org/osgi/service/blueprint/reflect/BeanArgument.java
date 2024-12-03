/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.Metadata;

public interface BeanArgument {
    public Metadata getValue();

    public String getValueType();

    public int getIndex();
}

