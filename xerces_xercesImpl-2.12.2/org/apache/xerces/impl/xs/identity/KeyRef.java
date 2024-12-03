/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.xs.XSIDCDefinition;

public class KeyRef
extends IdentityConstraint {
    protected final UniqueOrKey fKey;

    public KeyRef(String string, String string2, String string3, UniqueOrKey uniqueOrKey) {
        super(string, string2, string3);
        this.fKey = uniqueOrKey;
        this.type = (short)2;
    }

    public UniqueOrKey getKey() {
        return this.fKey;
    }

    @Override
    public XSIDCDefinition getRefKey() {
        return this.fKey;
    }
}

