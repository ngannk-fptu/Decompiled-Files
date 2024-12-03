/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.model.InternalEntity;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;

public class InternalEntityUtils {
    public static final int MAX_ENTITY_FIELD_LENGTH = 255;
    public static final Function<? super InternalEntity, String> GET_NAME = new Function<InternalEntity, String>(){

        public String apply(InternalEntity entity) {
            return entity.getName();
        }
    };

    private InternalEntityUtils() {
    }

    public static String truncateValue(String value) {
        return StringUtils.abbreviate((String)value, (int)255);
    }

    public static void validateLength(String value) {
        if (value != null && value.length() > 255) {
            throw new IllegalArgumentException("Value '" + value + "' exceeds maximum allowed length of " + 255 + " characters");
        }
    }
}

