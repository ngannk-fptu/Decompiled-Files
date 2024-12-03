/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.TypeRep;
import com.mchange.v1.identicator.Identicator;
import java.util.Arrays;

public class TypeRepIdenticator
implements Identicator {
    private static final TypeRepIdenticator INSTANCE = new TypeRepIdenticator();

    public static TypeRepIdenticator getInstance() {
        return INSTANCE;
    }

    private TypeRepIdenticator() {
    }

    @Override
    public boolean identical(Object object, Object object2) {
        if (object == object2) {
            return true;
        }
        TypeRep typeRep = (TypeRep)object;
        TypeRep typeRep2 = (TypeRep)object2;
        return typeRep.getTypeCode() == typeRep2.getTypeCode() && Arrays.equals(typeRep.getTypeSize(), typeRep2.getTypeSize());
    }

    @Override
    public int hash(Object object) {
        TypeRep typeRep = (TypeRep)object;
        int n = typeRep.getTypeCode();
        int[] nArray = typeRep.getTypeSize();
        if (nArray != null) {
            int n2 = nArray.length;
            for (int i = 0; i < n2; ++i) {
                n ^= nArray[i];
            }
            n ^= n2;
        }
        return n;
    }
}

