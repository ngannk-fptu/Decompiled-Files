/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.typeconversion.impl.converters;

import org.springframework.ldap.odm.typeconversion.impl.Converter;

public final class ToStringConverter
implements Converter {
    @Override
    public <T> T convert(Object source, Class<T> toClass) {
        return toClass.cast(source.toString());
    }
}

