/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

class OrNode
extends SyntaxNode {
    SyntaxNode _left;
    SyntaxNode _right;

    OrNode(SyntaxNode syntaxNode, SyntaxNode syntaxNode2) {
        this._left = syntaxNode;
        this._right = syntaxNode2;
    }

    boolean _nullable() {
        return this._left._nullable() || this._right._nullable();
    }

    BitSet _firstPosition() {
        BitSet bitSet = this._left._firstPosition();
        BitSet bitSet2 = this._right._firstPosition();
        BitSet bitSet3 = new BitSet(Math.max(bitSet.size(), bitSet2.size()));
        bitSet3.or(bitSet2);
        bitSet3.or(bitSet);
        return bitSet3;
    }

    BitSet _lastPosition() {
        BitSet bitSet = this._left._lastPosition();
        BitSet bitSet2 = this._right._lastPosition();
        BitSet bitSet3 = new BitSet(Math.max(bitSet.size(), bitSet2.size()));
        bitSet3.or(bitSet2);
        bitSet3.or(bitSet);
        return bitSet3;
    }

    void _followPosition(BitSet[] bitSetArray, SyntaxNode[] syntaxNodeArray) {
        this._left._followPosition(bitSetArray, syntaxNodeArray);
        this._right._followPosition(bitSetArray, syntaxNodeArray);
    }

    SyntaxNode _clone(int[] nArray) {
        return new OrNode(this._left._clone(nArray), this._right._clone(nArray));
    }
}

