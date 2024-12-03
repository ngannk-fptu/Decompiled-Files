/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.VariableRefBase;

public interface Closure {
    public boolean inInnerClass();

    public Closure getParentClosure();

    public String getInnerClassName();

    public void addVariable(VariableRefBase var1);
}

