/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.util.PrefixedName;
import java.util.BitSet;
import java.util.List;

public final class TokenModel
extends ModelNode {
    static final TokenModel NULL_TOKEN = new TokenModel(null);
    final PrefixedName mElemName;
    int mTokenIndex = -1;

    public TokenModel(PrefixedName elemName) {
        this.mElemName = elemName;
    }

    public static TokenModel getNullToken() {
        return NULL_TOKEN;
    }

    public PrefixedName getName() {
        return this.mElemName;
    }

    public ModelNode cloneModel() {
        return new TokenModel(this.mElemName);
    }

    public boolean isNullable() {
        return false;
    }

    public void indexTokens(List tokens) {
        if (this != NULL_TOKEN) {
            int index;
            this.mTokenIndex = index = tokens.size();
            tokens.add(this);
        }
    }

    public void addFirstPos(BitSet firstPos) {
        firstPos.set(this.mTokenIndex);
    }

    public void addLastPos(BitSet lastPos) {
        lastPos.set(this.mTokenIndex);
    }

    public void calcFollowPos(BitSet[] followPosSets) {
    }

    public String toString() {
        return this.mElemName == null ? "[null]" : this.mElemName.toString();
    }

    static {
        TokenModel.NULL_TOKEN.mTokenIndex = 0;
    }
}

