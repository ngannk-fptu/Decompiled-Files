/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.TokenModel;
import java.util.BitSet;
import java.util.List;

public class ChoiceModel
extends ModelNode {
    final ModelNode[] mSubModels;
    boolean mNullable = false;
    BitSet mFirstPos;
    BitSet mLastPos;

    protected ChoiceModel(ModelNode[] subModels) {
        this.mSubModels = subModels;
        boolean nullable = false;
        int len = subModels.length;
        for (int i = 0; i < len; ++i) {
            if (!subModels[i].isNullable()) continue;
            nullable = true;
            break;
        }
        this.mNullable = nullable;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSubModels.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mSubModels[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public ModelNode cloneModel() {
        int len = this.mSubModels.length;
        ModelNode[] newModels = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            newModels[i] = this.mSubModels[i].cloneModel();
        }
        return new ChoiceModel(newModels);
    }

    @Override
    public boolean isNullable() {
        return this.mNullable;
    }

    @Override
    public void indexTokens(List<TokenModel> tokens) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; ++i) {
            this.mSubModels[i].indexTokens(tokens);
        }
    }

    @Override
    public void addFirstPos(BitSet firstPos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            int len = this.mSubModels.length;
            for (int i = 0; i < len; ++i) {
                this.mSubModels[i].addFirstPos(this.mFirstPos);
            }
        }
        firstPos.or(this.mFirstPos);
    }

    @Override
    public void addLastPos(BitSet lastPos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            int len = this.mSubModels.length;
            for (int i = 0; i < len; ++i) {
                this.mSubModels[i].addLastPos(this.mLastPos);
            }
        }
        lastPos.or(this.mLastPos);
    }

    @Override
    public void calcFollowPos(BitSet[] followPosSets) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; ++i) {
            this.mSubModels[i].calcFollowPos(followPosSets);
        }
    }
}

