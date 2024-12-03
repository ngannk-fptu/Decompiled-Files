/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.Instruction;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;

public final class FilterGenerator
extends ClassGenerator {
    private static int TRANSLET_INDEX = 5;
    private final Instruction _aloadTranslet = new ALOAD(TRANSLET_INDEX);

    public FilterGenerator(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, Stylesheet stylesheet) {
        super(className, superClassName, fileName, accessFlags, interfaces, stylesheet);
    }

    @Override
    public final Instruction loadTranslet() {
        return this._aloadTranslet;
    }

    @Override
    public boolean isExternal() {
        return true;
    }
}

