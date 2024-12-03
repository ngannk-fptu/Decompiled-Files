/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.config.spi;

import org.hibernate.engine.config.spi.ConfigurationService;

public class StandardConverters {
    public static final ConfigurationService.Converter<Boolean> BOOLEAN = new ConfigurationService.Converter<Boolean>(){

        @Override
        public Boolean convert(Object value) {
            if (value == null) {
                throw new IllegalArgumentException("Null value passed to convert");
            }
            return Boolean.class.isInstance(value) ? (Boolean)Boolean.class.cast(value) : Boolean.parseBoolean(value.toString());
        }
    };
    public static final ConfigurationService.Converter<String> STRING = new ConfigurationService.Converter<String>(){

        @Override
        public String convert(Object value) {
            if (value == null) {
                throw new IllegalArgumentException("Null value passed to convert");
            }
            return value.toString();
        }
    };

    private StandardConverters() {
    }
}

