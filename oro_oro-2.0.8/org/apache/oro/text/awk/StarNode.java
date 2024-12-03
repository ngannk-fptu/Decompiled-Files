/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

class StarNode
extends SyntaxNode {
    SyntaxNode _left;

    StarNode(SyntaxNode syntaxNode) {
        this._left = syntaxNode;
    }

    boolean _nullable() {
        return true;
    }

    BitSet _firstPosition() {
        return this._left._firstPosition();
    }

    BitSet _lastPosition() {
        return this._left._lastPosition();
    }

    void _followPosition(BitSet[] bitSetArray, SyntaxNode[] syntaxNodeArray) {
        this._left._followPosition(bitSetArray, syntaxNodeArray);
        BitSet bitSet = this._lastPosition();
        BitSet bitSet2 = this._firstPosition();
        int n = bitSet.size();
        while (0 < n--) {
            if (!bitSet.get(n)) continue;
            bitSetArray[n].or(bitSet2);
        }
    }

    SyntaxNode _clone(int[] nArray) {
        return new StarNode(this._left._clone(nArray));
    }
}

