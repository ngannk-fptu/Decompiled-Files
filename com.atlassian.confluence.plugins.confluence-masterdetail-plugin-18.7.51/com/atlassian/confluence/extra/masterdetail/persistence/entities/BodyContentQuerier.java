/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail.persistence.entities;

import java.util.Collection;
import java.util.Map;

public interface BodyContentQuerier {
    public Map<Long, String> retrieveBodyContentForIds(Collection<Long> var1);
}

