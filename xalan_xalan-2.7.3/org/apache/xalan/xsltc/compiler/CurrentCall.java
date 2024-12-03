/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

final class CurrentCall
extends FunctionCall {
    public CurrentCall(QName fname) {
        super(fname);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        methodGen.getInstructionList().append(methodGen.loadCurrentNode());
    }
}

