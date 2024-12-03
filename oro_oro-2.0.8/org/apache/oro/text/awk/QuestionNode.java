/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import org.apache.oro.text.awk.EpsilonNode;
import org.apache.oro.text.awk.OrNode;
import org.apache.oro.text.awk.SyntaxNode;

final class QuestionNode
extends OrNode {
    static final SyntaxNode _epsilon = new EpsilonNode();

    QuestionNode(SyntaxNode syntaxNode) {
        super(syntaxNode, _epsilon);
    }

    boolean _nullable() {
        return true;
    }

    SyntaxNode _clone(int[] nArray) {
        return new QuestionNode(this._left._clone(nArray));
    }
}

