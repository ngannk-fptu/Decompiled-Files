/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestError
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FlowRequestErrorImpl
implements FlowRequestError,
Serializable {
    private static final long serialVersionUID = -615244461713920286L;
    private final String message;

    public FlowRequestErrorImpl(String message) {
        this.message = message;
    }

    @Nonnull
    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestErrorImpl that = (FlowRequestErrorImpl)o;
        return Objects.equals(this.message, that.message);
    }

    public int hashCode() {
        return Objects.hash(this.message);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("message", (Object)this.message).toString();
    }
}

