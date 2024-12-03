/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.core;

import org.springframework.cglib.core.ClassGenerator;

public interface GeneratorStrategy {
    public byte[] generate(ClassGenerator var1) throws Exception;

    public boolean equals(Object var1);
}

