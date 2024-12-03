/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.CompareGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.TestGenerator;

final class PositionCall
extends FunctionCall {
    public PositionCall(QName fname) {
        super(fname);
    }

    @Override
    public boolean hasPositionCall() {
        return true;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadCurrentNode());
        } else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(2));
        } else {
            ConstantPoolGen cpg = classGen.getConstantPool();
            int index = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "getPosition", "()I");
            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(index, 1));
        }
    }
}

