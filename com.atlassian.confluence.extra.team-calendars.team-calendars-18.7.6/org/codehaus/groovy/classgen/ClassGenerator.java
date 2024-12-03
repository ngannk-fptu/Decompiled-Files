/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovyjarjarasm.asm.Opcodes;
import java.util.LinkedList;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy.control.SourceUnit;

public abstract class ClassGenerator
extends ClassCodeVisitorSupport
implements Opcodes {
    protected LinkedList<ClassNode> innerClasses = new LinkedList();

    public LinkedList<ClassNode> getInnerClasses() {
        return this.innerClasses;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null;
    }

    public void visitBytecodeSequence(BytecodeSequence bytecodeSequence) {
    }
}

