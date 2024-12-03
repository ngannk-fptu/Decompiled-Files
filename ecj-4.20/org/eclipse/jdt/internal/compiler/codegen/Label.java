/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.codegen.CodeStream;

public abstract class Label {
    public CodeStream codeStream;
    public int position = -1;
    public static final int POS_NOT_SET = -1;

    public Label() {
    }

    public Label(CodeStream codeStream) {
        this.codeStream = codeStream;
    }

    public abstract void place();
}

