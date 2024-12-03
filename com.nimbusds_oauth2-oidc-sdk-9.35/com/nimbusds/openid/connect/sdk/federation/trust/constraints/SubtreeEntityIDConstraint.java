/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.trust.constraints;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.EntityIDConstraint;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public final class SubtreeEntityIDConstraint
extends EntityIDConstraint {
    private final String scheme;
    private final String hostNameAndRemainderPattern;

    public SubtreeEntityIDConstraint(String entityIDPattern) {
        if (entityIDPattern.startsWith("https://")) {
            this.scheme = "https://";
        } else if (entityIDPattern.startsWith("http://")) {
            this.scheme = "http://";
        } else {
            throw new IllegalArgumentException("The entity ID pattern must be an URI with https or http scheme");
        }
        this.hostNameAndRemainderPattern = entityIDPattern.substring(this.scheme.length());
        if (!this.hostNameAndRemainderPattern.startsWith(".")) {
            throw new IllegalArgumentException("The host part of the entity ID pattern must start with dot (.)");
        }
    }

    @Override
    public boolean matches(EntityID entityID) {
        String schemeIN;
        if (entityID.getValue().startsWith("https://")) {
            schemeIN = "https://";
        } else if (entityID.getValue().startsWith("http://")) {
            schemeIN = "http://";
        } else {
            return false;
        }
        if (!schemeIN.equals(this.scheme)) {
            return false;
        }
        String patternIN = entityID.getValue().substring(schemeIN.length());
        return patternIN.endsWith(this.hostNameAndRemainderPattern);
    }

    @Override
    public String toString() {
        return this.scheme + this.hostNameAndRemainderPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubtreeEntityIDConstraint)) {
            return false;
        }
        SubtreeEntityIDConstraint that = (SubtreeEntityIDConstraint)o;
        return this.scheme.equals(that.scheme) && this.hostNameAndRemainderPattern.equals(that.hostNameAndRemainderPattern);
    }

    public int hashCode() {
        return Objects.hash(this.scheme, this.hostNameAndRemainderPattern);
    }
}

