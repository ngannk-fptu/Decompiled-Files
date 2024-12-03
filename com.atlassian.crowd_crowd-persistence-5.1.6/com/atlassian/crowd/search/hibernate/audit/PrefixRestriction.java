/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.search.hibernate.audit.SimpleRestriction;
import com.google.common.annotations.VisibleForTesting;

public class PrefixRestriction
extends SimpleRestriction {
    public PrefixRestriction(String property, String value) {
        super(property, "LIKE", PrefixRestriction.sanitizePrefix(value) + "%");
    }

    @VisibleForTesting
    static String sanitizePrefix(String prefix) {
        return (Boolean)SystemProperties.AUDITLOG_SEARCH_ESCAPE_SPECIAL_CHARACTERS_ENABLED.getValue() != false ? prefix.replaceAll("[_]", "\\\\_").replaceAll("[%]", "") : prefix.replaceAll("[%_]", "");
    }
}

