/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.model.sso;

import com.atlassian.annotations.Internal;
import org.apache.commons.lang3.StringUtils;

@Internal
public enum NameIdFormat {
    UNSPECIFIED,
    EMAIL;


    public static NameIdFormat fromString(String nameIdFormatString) {
        try {
            if (StringUtils.isNotBlank((CharSequence)nameIdFormatString)) {
                return NameIdFormat.valueOf(nameIdFormatString.toUpperCase());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return UNSPECIFIED;
    }
}

