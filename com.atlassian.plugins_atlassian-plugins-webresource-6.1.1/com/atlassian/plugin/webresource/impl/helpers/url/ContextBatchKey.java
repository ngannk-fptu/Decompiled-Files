/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ContextBatchKey {
    private final List<String> included;
    private final LinkedHashSet<String> excluded;

    public ContextBatchKey(List<String> included, LinkedHashSet<String> excluded) {
        this.included = included;
        this.excluded = excluded;
    }

    public List<String> getIncluded() {
        return this.included;
    }

    public LinkedHashSet<String> getExcluded() {
        return this.excluded;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[').append(StringUtils.join(this.included, (String)", "));
        if (!this.excluded.isEmpty()) {
            buffer.append('-');
            buffer.append(StringUtils.join(this.excluded, (String)", "));
        }
        return buffer.append(']').toString();
    }
}

