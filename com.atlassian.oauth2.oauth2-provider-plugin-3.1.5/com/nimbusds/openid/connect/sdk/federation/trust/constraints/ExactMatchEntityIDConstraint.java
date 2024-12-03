/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust.constraints;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.EntityIDConstraint;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public final class ExactMatchEntityIDConstraint
extends EntityIDConstraint {
    private final EntityID entityID;

    public ExactMatchEntityIDConstraint(EntityID entityID) {
        if (entityID == null) {
            throw new IllegalArgumentException("The entity ID must not be null");
        }
        this.entityID = entityID;
    }

    @Override
    public boolean matches(EntityID entityID) {
        return this.entityID.equals(entityID);
    }

    @Override
    public String toString() {
        return this.entityID.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExactMatchEntityIDConstraint)) {
            return false;
        }
        ExactMatchEntityIDConstraint that = (ExactMatchEntityIDConstraint)o;
        return this.entityID.equals(that.entityID);
    }

    public int hashCode() {
        return Objects.hash(this.entityID);
    }
}

