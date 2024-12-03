/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

public interface LoadClass {
    public ObjectType getLoadClassType(ConstantPoolGen var1);

    public Type getType(ConstantPoolGen var1);
}

