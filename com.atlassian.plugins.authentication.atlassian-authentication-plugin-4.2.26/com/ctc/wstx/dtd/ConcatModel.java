/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.TokenModel;
import java.util.BitSet;
import java.util.List;

public class ConcatModel
extends ModelNode {
    ModelNode mLeftModel;
    ModelNode mRightModel;
    final boolean mNullable;
    BitSet mFirstPos;
    BitSet mLastPos;

    public ConcatModel(ModelNode left, ModelNode right) {
        this.mLeftModel = left;
        this.mRightModel = right;
        this.mNullable = this.mLeftModel.isNullable() && this.mRightModel.isNullable();
    }

    @Override
    public ModelNode cloneModel() {
        return new ConcatModel(this.mLeftModel.cloneModel(), this.mRightModel.cloneModel());
    }

    @Override
    public boolean isNullable() {
        return this.mNullable;
    }

    @Override
    public void indexTokens(List<TokenModel> tokens) {
        this.mLeftModel.indexTokens(tokens);
        this.mRightModel.indexTokens(tokens);
    }

    @Override
    public void addFirstPos(BitSet pos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            this.mLeftModel.addFirstPos(this.mFirstPos);
            if (this.mLeftModel.isNullable()) {
                this.mRightModel.addFirstPos(this.mFirstPos);
            }
        }
        pos.or(this.mFirstPos);
    }

    @Override
    public void addLastPos(BitSet pos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            this.mRightModel.addLastPos(this.mLastPos);
            if (this.mRightModel.isNullable()) {
                this.mLeftModel.addLastPos(this.mLastPos);
            }
        }
        pos.or(this.mLastPos);
    }

    @Override
    public void calcFollowPos(BitSet[] followPosSets) {
        this.mLeftModel.calcFollowPos(followPosSets);
        this.mRightModel.calcFollowPos(followPosSets);
        BitSet foll = new BitSet();
        this.mRightModel.addFirstPos(foll);
        BitSet toAddTo = new BitSet();
        this.mLeftModel.addLastPos(toAddTo);
        int ix = 0;
        while ((ix = toAddTo.nextSetBit(ix + 1)) >= 0) {
            followPosSets[ix].or(foll);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(this.mLeftModel.toString());
        sb.append(", ");
        sb.append(this.mRightModel.toString());
        sb.append(')');
        return sb.toString();
    }
}

