/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Type
 */
package net.sf.cglib.core;

import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.KeyFactoryCustomizer;
import org.objectweb.asm.Type;

public interface FieldTypeCustomizer
extends KeyFactoryCustomizer {
    public void customize(CodeEmitter var1, int var2, Type var3);

    public Type getOutType(int var1, Type var2);
}

