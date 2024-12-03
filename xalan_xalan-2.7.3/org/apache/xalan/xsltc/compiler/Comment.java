/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class Comment
extends Instruction {
    Comment() {
    }

    @Override
    public void parseContents(Parser parser) {
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.String;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Object content;
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        Text rawText = null;
        if (this.elementCount() == 1 && (content = this.elementAt(0)) instanceof Text) {
            rawText = (Text)content;
        }
        if (rawText != null) {
            il.append(methodGen.loadHandler());
            if (rawText.canLoadAsArrayOffsetLength()) {
                rawText.loadAsArrayOffsetLength(classGen, methodGen);
                int comment = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "comment", "([CII)V");
                il.append(new INVOKEINTERFACE(comment, 4));
            } else {
                il.append(new PUSH(cpg, rawText.getText()));
                int comment = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "comment", "(Ljava/lang/String;)V");
                il.append(new INVOKEINTERFACE(comment, 2));
            }
        } else {
            il.append(methodGen.loadHandler());
            il.append(DUP);
            il.append(classGen.loadTranslet());
            il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lorg/apache/xalan/xsltc/runtime/StringValueHandler;")));
            il.append(DUP);
            il.append(methodGen.storeHandler());
            this.translateContents(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(cpg.addMethodref("org.apache.xalan.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
            int comment = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "comment", "(Ljava/lang/String;)V");
            il.append(new INVOKEINTERFACE(comment, 2));
            il.append(methodGen.storeHandler());
        }
    }
}

