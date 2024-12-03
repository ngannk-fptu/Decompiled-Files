/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

public final class MatchGenerator
extends MethodGenerator {
    private static int CURRENT_INDEX = 1;
    private int _iteratorIndex = -1;
    private final Instruction _iloadCurrent = new ILOAD(CURRENT_INDEX);
    private final Instruction _istoreCurrent = new ISTORE(CURRENT_INDEX);
    private Instruction _aloadDom;

    public MatchGenerator(int access_flags, Type return_type, Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
    }

    @Override
    public Instruction loadCurrentNode() {
        return this._iloadCurrent;
    }

    @Override
    public Instruction storeCurrentNode() {
        return this._istoreCurrent;
    }

    public int getHandlerIndex() {
        return -1;
    }

    @Override
    public Instruction loadDOM() {
        return this._aloadDom;
    }

    public void setDomIndex(int domIndex) {
        this._aloadDom = new ALOAD(domIndex);
    }

    public int getIteratorIndex() {
        return this._iteratorIndex;
    }

    public void setIteratorIndex(int iteratorIndex) {
        this._iteratorIndex = iteratorIndex;
    }

    @Override
    public int getLocalIndex(String name) {
        if (name.equals("current")) {
            return CURRENT_INDEX;
        }
        return super.getLocalIndex(name);
    }
}

