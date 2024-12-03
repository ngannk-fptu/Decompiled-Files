/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust.constraints;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.ExactMatchEntityIDConstraint;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.SubtreeEntityIDConstraint;

public abstract class EntityIDConstraint {
    public abstract boolean matches(EntityID var1);

    public abstract String toString();

    public abstract boolean equals(Object var1);

    public static EntityIDConstraint parse(String value) throws ParseException {
        try {
            return new SubtreeEntityIDConstraint(value);
        }
        catch (IllegalArgumentException e) {
            try {
                return new ExactMatchEntityIDConstraint(new EntityID(value));
            }
            catch (IllegalArgumentException e2) {
                throw new ParseException(e2.getMessage(), e2);
            }
        }
    }
}

