/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
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

