/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Util;

public final class AttributeSetMethodGenerator
extends MethodGenerator {
    private static final int DOM_INDEX = 1;
    private static final int ITERATOR_INDEX = 2;
    private static final int HANDLER_INDEX = 3;
    private static final Type[] argTypes = new Type[3];
    private static final String[] argNames = new String[3];
    private final Instruction _aloadDom = new ALOAD(1);
    private final Instruction _astoreDom = new ASTORE(1);
    private final Instruction _astoreIterator = new ASTORE(2);
    private final Instruction _aloadIterator = new ALOAD(2);
    private final Instruction _astoreHandler = new ASTORE(3);
    private final Instruction _aloadHandler = new ALOAD(3);

    public AttributeSetMethodGenerator(String methodName, ClassGen classGen) {
        super(2, Type.VOID, argTypes, argNames, methodName, classGen.getClassName(), new InstructionList(), classGen.getConstantPool());
    }

    @Override
    public Instruction storeIterator() {
        return this._astoreIterator;
    }

    @Override
    public Instruction loadIterator() {
        return this._aloadIterator;
    }

    public int getIteratorIndex() {
        return 2;
    }

    @Override
    public Instruction storeHandler() {
        return this._astoreHandler;
    }

    @Override
    public Instruction loadHandler() {
        return this._aloadHandler;
    }

    @Override
    public int getLocalIndex(String name) {
        return -1;
    }

    static {
        AttributeSetMethodGenerator.argTypes[0] = Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;");
        AttributeSetMethodGenerator.argNames[0] = "dom";
        AttributeSetMethodGenerator.argTypes[1] = Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;");
        AttributeSetMethodGenerator.argNames[1] = "iterator";
        AttributeSetMethodGenerator.argTypes[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);
        AttributeSetMethodGenerator.argNames[2] = "handler";
    }
}

