/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

final class GenerateIdCall
extends FunctionCall {
    public GenerateIdCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        if (this.argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
        } else {
            this.argument().translate(classGen, methodGen);
        }
        ConstantPoolGen cpg = classGen.getConstantPool();
        il.append(new INVOKESTATIC(cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "generate_idF", "(I)Ljava/lang/String;")));
    }
}

