/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassVisitor
 */
package net.sf.cglib.core;

import org.objectweb.asm.ClassVisitor;

public interface ClassGenerator {
    public void generateClass(ClassVisitor var1) throws Exception;
}

