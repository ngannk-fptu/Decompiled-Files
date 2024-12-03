/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;

@Deprecated
public class IndexFieldMappingConflictException
extends RuntimeException {
    private static final long serialVersionUID = -6735028616263409341L;

    public IndexFieldMappingConflictException(FieldMapping current, FieldMapping existing) {
        super(String.format("Mapping for %s (%s) conflicts with existing mapping (%s).", current.getName(), current, existing));
    }
}

