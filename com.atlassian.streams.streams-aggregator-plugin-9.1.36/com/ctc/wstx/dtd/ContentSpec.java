/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.StructValidator;

public abstract class ContentSpec {
    protected char mArity;

    public ContentSpec(char arity) {
        this.mArity = arity;
    }

    public final char getArity() {
        return this.mArity;
    }

    public final void setArity(char c) {
        this.mArity = c;
    }

    public boolean isLeaf() {
        return false;
    }

    public abstract StructValidator getSimpleValidator();

    public abstract ModelNode rewrite();
}

