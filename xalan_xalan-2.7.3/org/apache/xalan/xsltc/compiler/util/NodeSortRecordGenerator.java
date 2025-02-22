/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.Instruction;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;

public final class NodeSortRecordGenerator
extends ClassGenerator {
    private static final int TRANSLET_INDEX = 4;
    private final Instruction _aloadTranslet = new ALOAD(4);

    public NodeSortRecordGenerator(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
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

