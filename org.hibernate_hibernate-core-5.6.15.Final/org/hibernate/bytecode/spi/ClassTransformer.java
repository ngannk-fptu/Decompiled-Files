/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.ClassTransformer
 */
package org.hibernate.bytecode.spi;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface ClassTransformer
extends javax.persistence.spi.ClassTransformer {
    public byte[] transform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5) throws IllegalClassFormatException;
}

