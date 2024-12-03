/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.EnglishEnums
 */
package org.apache.logging.log4j.core.config.plugins.convert;

import org.apache.logging.log4j.core.config.plugins.convert.TypeConverter;
import org.apache.logging.log4j.util.EnglishEnums;

public class EnumConverter<E extends Enum<E>>
implements TypeConverter<E> {
    private final Class<E> clazz;

    public EnumConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convert(String s) {
        return (E)EnglishEnums.valueOf(this.clazz, (String)s);
    }
}

