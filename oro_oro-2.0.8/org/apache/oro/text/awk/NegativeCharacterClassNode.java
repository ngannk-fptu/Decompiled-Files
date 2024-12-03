/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.CharacterClassNode;
import org.apache.oro.text.awk.SyntaxNode;

final class NegativeCharacterClassNode
extends CharacterClassNode {
    NegativeCharacterClassNode(int n) {
        super(n);
        this._characterSet.set(256);
    }

    boolean _matches(char c) {
        return !this._characterSet.get(c);
    }

    SyntaxNode _clone(int[] nArray) {
        int n = nArray[0];
        nArray[0] = n + 1;
        NegativeCharacterClassNode negativeCharacterClassNode = new NegativeCharacterClassNode(n);
        negativeCharacterClassNode._characterSet = (BitSet)this._characterSet.clone();
        return negativeCharacterClassNode;
    }
}

