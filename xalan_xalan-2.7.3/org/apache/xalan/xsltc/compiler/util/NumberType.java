/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.util.Type;

public abstract class NumberType
extends Type {
    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isSimple() {
        return true;
    }
}

