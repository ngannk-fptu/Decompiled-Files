/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Otherwise;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.When;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Choose
extends Instruction {
    Choose() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("Choose");
        this.indent(indent + 4);
        this.displayContents(indent + 4);
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ArrayList whenElements = new ArrayList();
        Otherwise otherwise = null;
        Enumeration elements = this.elements();
        ErrorMsg error = null;
        int line = this.getLineNumber();
        while (elements.hasMoreElements()) {
            Object element = elements.nextElement();
            if (element instanceof When) {
                whenElements.add(element);
                continue;
            }
            if (element instanceof Otherwise) {
                if (otherwise == null) {
                    otherwise = (Otherwise)element;
                    continue;
                }
                error = new ErrorMsg("MULTIPLE_OTHERWISE_ERR", this);
                this.getParser().reportError(3, error);
                continue;
            }
            if (element instanceof Text) {
                ((Text)element).ignore();
                continue;
            }
            error = new ErrorMsg("WHEN_ELEMENT_ERR", this);
            this.getParser().reportError(3, error);
        }
        if (whenElements.size() == 0) {
            error = new ErrorMsg("MISSING_WHEN_ERR", this);
            this.getParser().reportError(3, error);
            return;
        }
        InstructionList il = methodGen.getInstructionList();
        BranchHandle nextElement = null;
        ArrayList<BranchHandle> exitHandles = new ArrayList<BranchHandle>();
        InstructionHandle exit = null;
        Iterator whens = whenElements.iterator();
        while (whens.hasNext()) {
            When when = (When)whens.next();
            Expression test = when.getTest();
            InstructionHandle truec = il.getEnd();
            if (nextElement != null) {
                nextElement.setTarget(il.append(NOP));
            }
            test.translateDesynthesized(classGen, methodGen);
            if (test instanceof FunctionCall) {
                FunctionCall call = (FunctionCall)test;
                try {
                    Type type = call.typeCheck(this.getParser().getSymbolTable());
                    if (type != Type.Boolean) {
                        test._falseList.add(il.append(new IFEQ(null)));
                    }
                }
                catch (TypeCheckError typeCheckError) {
                    // empty catch block
                }
            }
            truec = il.getEnd();
            if (!when.ignore()) {
                when.translateContents(classGen, methodGen);
            }
            exitHandles.add(il.append(new GOTO(null)));
            if (whens.hasNext() || otherwise != null) {
                nextElement = il.append(new GOTO(null));
                test.backPatchFalseList(nextElement);
            } else {
                exit = il.append(NOP);
                test.backPatchFalseList(exit);
            }
            test.backPatchTrueList(truec.getNext());
        }
        if (otherwise != null) {
            nextElement.setTarget(il.append(NOP));
            otherwise.translateContents(classGen, methodGen);
            exit = il.append(NOP);
        }
        for (BranchHandle gotoExit : exitHandles) {
            gotoExit.setTarget(exit);
        }
    }
}

