/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.Type;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Util;

public final class CompareGenerator
extends MethodGenerator {
    private static int DOM_INDEX = 1;
    private static int CURRENT_INDEX = 2;
    private static int LEVEL_INDEX = 3;
    private static int TRANSLET_INDEX = 4;
    private static int LAST_INDEX = 5;
    private int ITERATOR_INDEX = 6;
    private final Instruction _iloadCurrent = new ILOAD(CURRENT_INDEX);
    private final Instruction _istoreCurrent = new ISTORE(CURRENT_INDEX);
    private final Instruction _aloadDom = new ALOAD(DOM_INDEX);
    private final Instruction _iloadLast = new ILOAD(LAST_INDEX);
    private final Instruction _aloadIterator;
    private final Instruction _astoreIterator;

    public CompareGenerator(int access_flags, Type return_type, Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
        LocalVariableGen iterator = this.addLocalVariable("iterator", Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), null, null);
        this.ITERATOR_INDEX = iterator.getIndex();
        this._aloadIterator = new ALOAD(this.ITERATOR_INDEX);
        this._astoreIterator = new ASTORE(this.ITERATOR_INDEX);
        il.append(new ACONST_NULL());
        il.append(this.storeIterator());
    }

    public Instruction loadLastNode() {
        return this._iloadLast;
    }

    @Override
    public Instruction loadCurrentNode() {
        return this._iloadCurrent;
    }

    @Override
    public Instruction storeCurrentNode() {
        return this._istoreCurrent;
    }

    @Override
    public Instruction loadDOM() {
        return this._aloadDom;
    }

    public int getHandlerIndex() {
        return -1;
    }

    public int getIteratorIndex() {
        return -1;
    }

    @Override
    public Instruction storeIterator() {
        return this._astoreIterator;
    }

    @Override
    public Instruction loadIterator() {
        return this._aloadIterator;
    }

    @Override
    public int getLocalIndex(String name) {
        if (name.equals("current")) {
            return CURRENT_INDEX;
        }
        return super.getLocalIndex(name);
    }
}

