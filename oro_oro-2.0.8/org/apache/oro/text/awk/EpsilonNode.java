/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

final class EpsilonNode
extends SyntaxNode {
    BitSet _positionSet = new BitSet(1);

    EpsilonNode() {
    }

    boolean _nullable() {
        return true;
    }

    BitSet _firstPosition() {
        return this._positionSet;
    }

    BitSet _lastPosition() {
        return this._positionSet;
    }

    void _followPosition(BitSet[] bitSetArray, SyntaxNode[] syntaxNodeArray) {
    }

    SyntaxNode _clone(int[] nArray) {
        return new EpsilonNode();
    }
}

