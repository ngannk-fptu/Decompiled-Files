/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.migration.agent.store.jpa;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.CollectionUtils;

@ParametersAreNonnullByDefault
public interface Page<T> {
    public List<T> getContent();

    public Page<T> next();

    default public boolean hasContent() {
        return CollectionUtils.isNotEmpty(this.getContent());
    }
}

