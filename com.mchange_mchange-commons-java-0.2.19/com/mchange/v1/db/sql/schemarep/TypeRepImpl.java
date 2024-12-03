/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.TypeRep;
import com.mchange.v1.db.sql.schemarep.TypeRepIdenticator;

public final class TypeRepImpl
implements TypeRep {
    int type_code;
    int[] typeSize;

    public TypeRepImpl(int n, int[] nArray) {
        this.type_code = n;
        this.typeSize = nArray;
    }

    @Override
    public int getTypeCode() {
        return this.type_code;
    }

    @Override
    public int[] getTypeSize() {
        return this.typeSize;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof TypeRep) {
            return TypeRepIdenticator.getInstance().identical(this, object);
        }
        return false;
    }

    public int hashCode() {
        return TypeRepIdenticator.getInstance().hash(this);
    }
}

