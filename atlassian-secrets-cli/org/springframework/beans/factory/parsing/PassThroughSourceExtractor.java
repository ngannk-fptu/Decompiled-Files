/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class PassThroughSourceExtractor
implements SourceExtractor {
    @Override
    public Object extractSource(Object sourceCandidate, @Nullable Resource definingResource) {
        return sourceCandidate;
    }
}

