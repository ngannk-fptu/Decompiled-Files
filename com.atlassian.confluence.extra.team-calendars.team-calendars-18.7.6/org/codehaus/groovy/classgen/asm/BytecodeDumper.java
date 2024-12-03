/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.util.TraceClassVisitor;
import java.io.PrintWriter;
import java.io.Writer;
import org.codehaus.groovy.control.BytecodeProcessor;

public class BytecodeDumper
implements BytecodeProcessor {
    public static final BytecodeDumper STANDARD_ERR = new BytecodeDumper(new PrintWriter(System.err));
    private final Writer out;

    public BytecodeDumper(Writer out) {
        this.out = out;
    }

    @Override
    public byte[] processBytecode(String name, byte[] original) {
        PrintWriter pw = this.out instanceof PrintWriter ? (PrintWriter)this.out : new PrintWriter(this.out);
        TraceClassVisitor visitor = new TraceClassVisitor(pw);
        ClassReader reader = new ClassReader(original);
        reader.accept(visitor, 0);
        return original;
    }
}

