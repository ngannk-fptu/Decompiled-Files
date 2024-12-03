/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.IdPattern;
import org.apache.xalan.xsltc.compiler.LocationPathPattern;
import org.apache.xalan.xsltc.compiler.RelativePathPattern;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

abstract class IdKeyPattern
extends LocationPathPattern {
    protected RelativePathPattern _left = null;
    private String _index = null;
    private String _value = null;

    public IdKeyPattern(String index, String value) {
        this._index = index;
        this._value = value;
    }

    public String getIndexName() {
        return this._index;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.NodeSet;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    public void setLeft(RelativePathPattern left) {
        this._left = left;
    }

    @Override
    public StepPattern getKernelPattern() {
        return null;
    }

    @Override
    public void reduceKernelPattern() {
    }

    @Override
    public String toString() {
        return "id/keyPattern(" + this._index + ", " + this._value + ')';
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int getKeyIndex = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lorg/apache/xalan/xsltc/dom/KeyIndex;");
        int lookupId = cpg.addMethodref("org/apache/xalan/xsltc/dom/KeyIndex", "containsID", "(ILjava/lang/Object;)I");
        int lookupKey = cpg.addMethodref("org/apache/xalan/xsltc/dom/KeyIndex", "containsKey", "(ILjava/lang/Object;)I");
        int getNodeIdent = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getNodeIdent", "(I)I");
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, this._index));
        il.append(new INVOKEVIRTUAL(getKeyIndex));
        il.append(SWAP);
        il.append(new PUSH(cpg, this._value));
        if (this instanceof IdPattern) {
            il.append(new INVOKEVIRTUAL(lookupId));
        } else {
            il.append(new INVOKEVIRTUAL(lookupKey));
        }
        this._trueList.add(il.append(new IFNE(null)));
        this._falseList.add(il.append(new GOTO(null)));
    }
}

