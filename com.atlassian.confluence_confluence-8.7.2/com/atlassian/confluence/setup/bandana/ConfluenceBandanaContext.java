/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.confluence.spaces.Space;

public class ConfluenceBandanaContext
implements KeyedBandanaContext {
    String spaceKey;
    public static final ConfluenceBandanaContext GLOBAL_CONTEXT = new ConfluenceBandanaContext();
    private static final String GLOBAL_CONTEXT_KEY = "_GLOBAL";

    public ConfluenceBandanaContext() {
    }

    public ConfluenceBandanaContext(Space space) {
        if (space != null) {
            this.spaceKey = space.getKey();
        }
    }

    public ConfluenceBandanaContext(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public BandanaContext getParentContext() {
        if (this.spaceKey != null) {
            return GLOBAL_CONTEXT;
        }
        return null;
    }

    public boolean hasParentContext() {
        return this.spaceKey != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfluenceBandanaContext)) {
            return false;
        }
        ConfluenceBandanaContext confluenceBandanaContext = (ConfluenceBandanaContext)o;
        return !(this.spaceKey != null ? !this.spaceKey.equals(confluenceBandanaContext.spaceKey) : confluenceBandanaContext.spaceKey != null);
    }

    public int hashCode() {
        return this.spaceKey != null ? this.spaceKey.hashCode() : 0;
    }

    public String toString() {
        return "[" + super.toString() + ", spaceKey='" + this.spaceKey + "']";
    }

    @Override
    public String getContextKey() {
        if (this.spaceKey != null) {
            return this.spaceKey;
        }
        return GLOBAL_CONTEXT_KEY;
    }

    public boolean isGlobal() {
        return this.spaceKey == null;
    }
}

