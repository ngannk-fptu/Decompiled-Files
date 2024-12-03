/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.api;

import com.atlassian.confluence.core.persistence.schema.api.SchemaComparison;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface SchemaComparisonService {
    public SchemaComparison compareExpectedWithActualSchema() throws Exception;
}

