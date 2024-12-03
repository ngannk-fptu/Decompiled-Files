/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class ConcatCall
extends FunctionCall {
    public ConcatCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        for (int i = 0; i < this.argumentCount(); ++i) {
            Expression exp = this.argument(i);
            if (exp.typeCheck(stable).identicalTo(Type.String)) continue;
            this.setArgument(i, new CastExpr(exp, Type.String));
        }
        this._type = Type.String;
        return this._type;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int nArgs = this.argumentCount();
        switch (nArgs) {
            case 0: {
                il.append(new PUSH(cpg, ""));
                break;
            }
            case 1: {
                this.argument().translate(classGen, methodGen);
                break;
            }
            default: {
                int initBuffer = cpg.addMethodref("java.lang.StringBuffer", "<init>", "()V");
                INVOKEVIRTUAL append = new INVOKEVIRTUAL(cpg.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
                int toString = cpg.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
                il.append(new NEW(cpg.addClass("java.lang.StringBuffer")));
                il.append(DUP);
                il.append(new INVOKESPECIAL(initBuffer));
                for (int i = 0; i < nArgs; ++i) {
                    this.argument(i).translate(classGen, methodGen);
                    il.append(append);
                }
                il.append(new INVOKEVIRTUAL(toString));
            }
        }
    }
}

