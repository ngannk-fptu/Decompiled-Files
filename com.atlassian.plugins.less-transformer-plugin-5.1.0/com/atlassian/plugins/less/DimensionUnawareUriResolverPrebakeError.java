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
import javax.annotation.Nonnull;

public class DimensionUnawareUriResolverPrebakeError<E>
implements PrebakeError {
    private final E source;

    public DimensionUnawareUriResolverPrebakeError(@Nonnull E source) {
        this.source = Preconditions.checkNotNull(source);
    }

    public String toString() {
        return "Encountered dimension unaware transformation: " + this.source.getClass().getSimpleName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DimensionUnawareUriResolverPrebakeError)) {
            return false;
        }
        DimensionUnawareUriResolverPrebakeError that = (DimensionUnawareUriResolverPrebakeError)o;
        return this.source.equals(that.source);
    }

    public int hashCode() {
        return this.source.hashCode();
    }
}

