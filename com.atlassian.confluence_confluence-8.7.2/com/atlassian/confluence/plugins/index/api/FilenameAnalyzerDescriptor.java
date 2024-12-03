/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import java.util.Objects;

public class FilenameAnalyzerDescriptor
implements MappingAnalyzerDescriptor {
    public boolean equals(Object obj) {
        return obj instanceof FilenameAnalyzerDescriptor;
    }

    public int hashCode() {
        return Objects.hashCode(FilenameAnalyzerDescriptor.class);
    }
}

