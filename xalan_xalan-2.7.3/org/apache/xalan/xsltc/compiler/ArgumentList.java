/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Expression;

final class ArgumentList {
    private final Expression _arg;
    private final ArgumentList _rest;

    public ArgumentList(Expression arg, ArgumentList rest) {
        this._arg = arg;
        this._rest = rest;
    }

    public String toString() {
        return this._rest == null ? this._arg.toString() : this._arg.toString() + ", " + this._rest.toString();
    }
}

