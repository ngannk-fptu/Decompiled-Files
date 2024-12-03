/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import java.util.Objects;

public class ExactAnalyzerDescriptor
implements MappingAnalyzerDescriptor {
    public int hashCode() {
        return Objects.hash(this.getClass());
    }

    public boolean equals(Object o) {
        return o != null && o.getClass() == this.getClass();
    }
}

