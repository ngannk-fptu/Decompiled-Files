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

final class LastCall
extends FunctionCall {
    public LastCall(QName fname) {
        super(fname);
    }

    @Override
    public boolean hasPositionCall() {
        return true;
    }

    @Override
    public boolean hasLastCall() {
        return true;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadLastNode());
        } else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(3));
        } else {
            ConstantPoolGen cpg = classGen.getConstantPool();
            int getLast = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "getLast", "()I");
            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(getLast, 1));
        }
    }
}

