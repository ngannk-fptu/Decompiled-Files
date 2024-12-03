/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.Instruction;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;

public final class NodeCounterGenerator
extends ClassGenerator {
    private Instruction _aloadTranslet;

    public NodeCounterGenerator(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
    }

    public void setTransletIndex(int index) {
        this._aloadTranslet = new ALOAD(index);
    }

    @Override
    public Instruction loadTranslet() {
        return this._aloadTranslet;
    }

    @Override
    public boolean isExternal() {
        return true;
    }
}

