/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.confluence.setup.bandana.KeyedBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.confluence.spaces.Space;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class SpaceBandanaContext
implements KeyedBandanaContext {
    private final Space space;

    public SpaceBandanaContext(Space space) {
        if (space == null) {
            throw new IllegalArgumentException("space is required");
        }
        if (StringUtils.isBlank((CharSequence)space.getKey())) {
            throw new IllegalArgumentException("space must have a non-empty spaceKey.");
        }
        this.space = space;
    }

    public String getContextKey() {
        return "com.atlassian.confluence.blueprints.plugin-module-state:" + this.space.getKey();
    }

    @Nullable
    public BandanaContext getParentContext() {
        return null;
    }

    public boolean hasParentContext() {
        return false;
    }

    public boolean equals(Object that) {
        return that instanceof SpaceBandanaContext && this.getContextKey().equals(((SpaceBandanaContext)that).getContextKey());
    }

    public int hashCode() {
        return this.getContextKey().hashCode();
    }
}

