/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.ClassGenerator;

public class BytecodeSequence
extends Statement {
    private final List<BytecodeInstruction> instructions;

    public BytecodeSequence(List instructions) {
        this.instructions = instructions;
    }

    public BytecodeSequence(BytecodeInstruction instruction) {
        this.instructions = new ArrayList<BytecodeInstruction>(1);
        this.instructions.add(instruction);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        if (visitor instanceof ClassGenerator) {
            ClassGenerator gen = (ClassGenerator)visitor;
            gen.visitBytecodeSequence(this);
            return;
        }
        for (BytecodeInstruction part : this.instructions) {
            if (!(part instanceof ASTNode)) continue;
            ((ASTNode)((Object)part)).visit(visitor);
        }
    }

    public List getInstructions() {
        return this.instructions;
    }
}

