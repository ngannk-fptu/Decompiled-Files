/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.core;

import net.sf.cglib.core.ClassGenerator;

public interface GeneratorStrategy {
    public byte[] generate(ClassGenerator var1) throws Exception;

    public boolean equals(Object var1);
}

