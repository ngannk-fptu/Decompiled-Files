/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.LeafNode;
import org.apache.oro.text.awk.SyntaxNode;

final class SyntaxTree {
    int _positions;
    SyntaxNode _root;
    LeafNode[] _nodes;
    BitSet[] _followSet;

    SyntaxTree(SyntaxNode syntaxNode, int n) {
        this._root = syntaxNode;
        this._positions = n;
    }

    void _computeFollowPositions() {
        this._followSet = new BitSet[this._positions];
        this._nodes = new LeafNode[this._positions];
        int n = this._positions;
        while (0 < n--) {
            this._followSet[n] = new BitSet(this._positions);
        }
        this._root._followPosition(this._followSet, this._nodes);
    }

    private void __addToFastMap(BitSet bitSet, boolean[] blArray, boolean[] blArray2) {
        for (int i = 0; i < this._positions; ++i) {
            if (!bitSet.get(i) || blArray2[i]) continue;
            blArray2[i] = true;
            for (int j = 0; j < 256; ++j) {
                if (blArray[j]) continue;
                blArray[j] = this._nodes[i]._matches((char)j);
            }
        }
    }

    boolean[] createFastMap() {
        boolean[] blArray = new boolean[256];
        boolean[] blArray2 = new boolean[this._positions];
        this.__addToFastMap(this._root._firstPosition(), blArray, blArray2);
        return blArray;
    }
}

