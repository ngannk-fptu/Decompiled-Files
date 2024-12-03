/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;

public abstract class XmlFragment
extends AstNode {
    public XmlFragment() {
        this.type = 149;
    }

    public XmlFragment(int pos) {
        super(pos);
        this.type = 149;
    }

    public XmlFragment(int pos, int len) {
        super(pos, len);
        this.type = 149;
    }
}

