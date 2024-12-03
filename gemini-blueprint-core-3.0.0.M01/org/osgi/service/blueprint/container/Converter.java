/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

import org.osgi.service.blueprint.container.ReifiedType;

public interface Converter {
    public boolean canConvert(Object var1, ReifiedType var2);

    public Object convert(Object var1, ReifiedType var2) throws Exception;
}

