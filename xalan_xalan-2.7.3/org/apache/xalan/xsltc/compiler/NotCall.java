/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

final class NotCall
extends FunctionCall {
    public NotCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        this.argument().translate(classGen, methodGen);
        il.append(ICONST_1);
        il.append(IXOR);
    }

    @Override
    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        Expression exp = this.argument();
        exp.translateDesynthesized(classGen, methodGen);
        BranchHandle gotoh = il.append(new GOTO(null));
        this._trueList = exp._falseList;
        this._falseList = exp._trueList;
        this._falseList.add(gotoh);
    }
}

