/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import org.apache.oro.text.awk.LeafNode;
import org.apache.oro.text.awk.SyntaxNode;

class TokenNode
extends LeafNode {
    char _token;

    TokenNode(char c, int n) {
        super(n);
        this._token = c;
    }

    boolean _matches(char c) {
        return this._token == c;
    }

    SyntaxNode _clone(int[] nArray) {
        int n = nArray[0];
        nArray[0] = n + 1;
        return new TokenNode(this._token, n);
    }
}

