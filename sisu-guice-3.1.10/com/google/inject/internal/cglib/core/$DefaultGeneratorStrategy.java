/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.cglib.core.$ClassGenerator;
import com.google.inject.internal.cglib.core.$DebuggingClassWriter;
import com.google.inject.internal.cglib.core.$GeneratorStrategy;

public class $DefaultGeneratorStrategy
implements $GeneratorStrategy {
    public static final $DefaultGeneratorStrategy INSTANCE = new $DefaultGeneratorStrategy();

    public byte[] generate($ClassGenerator cg) throws Exception {
        $DebuggingClassWriter cw = this.getClassVisitor();
        this.transform(cg).generateClass(cw);
        return this.transform(cw.toByteArray());
    }

    protected $DebuggingClassWriter getClassVisitor() throws Exception {
        return new $DebuggingClassWriter(1);
    }

    protected final $ClassWriter getClassWriter() {
        throw new UnsupportedOperationException("You are calling getClassWriter, which no longer exists in this cglib version.");
    }

    protected byte[] transform(byte[] b) throws Exception {
        return b;
    }

    protected $ClassGenerator transform($ClassGenerator cg) throws Exception {
        return cg;
    }
}

