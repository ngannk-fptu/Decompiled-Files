/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.config.spi;

import java.util.Map;
import org.hibernate.service.Service;

public interface ConfigurationService
extends Service {
    public Map getSettings();

    public <T> T getSetting(String var1, Converter<T> var2);

    public <T> T getSetting(String var1, Converter<T> var2, T var3);

    public <T> T getSetting(String var1, Class<T> var2, T var3);

    @Deprecated
    public <T> T cast(Class<T> var1, Object var2);

    public static interface Converter<T> {
        public T convert(Object var1);
    }
}

