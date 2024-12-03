/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;

public class UninitializedObjectType
extends ReferenceType
implements Constants {
    private final ObjectType initialized;

    public UninitializedObjectType(ObjectType objectType) {
        super((byte)15, "<UNINITIALIZED OBJECT OF TYPE '" + objectType.getClassName() + "'>");
        this.initialized = objectType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UninitializedObjectType)) {
            return false;
        }
        return this.initialized.equals(((UninitializedObjectType)o).initialized);
    }

    public ObjectType getInitialized() {
        return this.initialized;
    }

    @Override
    public int hashCode() {
        return this.initialized.hashCode();
    }
}

