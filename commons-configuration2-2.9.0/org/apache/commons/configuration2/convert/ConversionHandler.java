/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.convert;

import java.util.Collection;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;

public interface ConversionHandler {
    public <T> T to(Object var1, Class<T> var2, ConfigurationInterpolator var3);

    public Object toArray(Object var1, Class<?> var2, ConfigurationInterpolator var3);

    public <T> void toCollection(Object var1, Class<T> var2, ConfigurationInterpolator var3, Collection<T> var4);
}

