/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.typeconversion.impl.converters;

import java.lang.reflect.Constructor;
import org.springframework.ldap.odm.typeconversion.impl.Converter;

public final class FromStringConverter
implements Converter {
    @Override
    public <T> T convert(Object source, Class<T> toClass) throws Exception {
        Constructor<T> constructor = toClass.getConstructor(String.class);
        return constructor.newInstance(source);
    }
}

