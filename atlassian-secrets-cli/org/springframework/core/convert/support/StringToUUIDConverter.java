/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

final class StringToUUIDConverter
implements Converter<String, UUID> {
    StringToUUIDConverter() {
    }

    @Override
    public UUID convert(String source) {
        return StringUtils.hasLength(source) ? UUID.fromString(source.trim()) : null;
    }
}

