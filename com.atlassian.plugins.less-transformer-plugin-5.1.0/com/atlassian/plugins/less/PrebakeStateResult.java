/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.less;

import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nonnull;

public class PrebakeStateResult {
    private final String state;
    private final List<PrebakeError> prebakeErrors;

    public PrebakeStateResult(@Nonnull String state, @Nonnull List<PrebakeError> prebakeErrors) {
        this.state = (String)Preconditions.checkNotNull((Object)state);
        this.prebakeErrors = (List)Preconditions.checkNotNull(prebakeErrors);
    }

    public String getState() {
        return this.state;
    }

    public List<PrebakeError> getPrebakeErrors() {
        return this.prebakeErrors;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrebakeStateResult)) {
            return false;
        }
        PrebakeStateResult that = (PrebakeStateResult)o;
        if (!this.state.equals(that.state)) {
            return false;
        }
        return this.prebakeErrors.equals(that.prebakeErrors);
    }

    public int hashCode() {
        int result = this.state.hashCode();
        result = 31 * result + this.prebakeErrors.hashCode();
        return result;
    }
}

