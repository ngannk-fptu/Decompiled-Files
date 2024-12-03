/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.annotations.Internal;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;

@Internal
public class JournalBandanaContext
implements KeyedBandanaContext {
    @Override
    public String getContextKey() {
        return JournalBandanaContext.class.getName();
    }

    public BandanaContext getParentContext() {
        return null;
    }

    public boolean hasParentContext() {
        return false;
    }
}

