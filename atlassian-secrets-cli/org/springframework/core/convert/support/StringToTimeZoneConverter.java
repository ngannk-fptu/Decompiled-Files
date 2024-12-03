/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

class StringToTimeZoneConverter
implements Converter<String, TimeZone> {
    StringToTimeZoneConverter() {
    }

    @Override
    public TimeZone convert(String source) {
        return StringUtils.parseTimeZoneString(source);
    }
}

