/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
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
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.mSubModels.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mSubModels[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

    public ModelNode cloneModel() {
        int len = this.mSubModels.length;
        ModelNode[] newModels = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            newModels[i] = this.mSubModels[i].cloneModel();
        }
        return new ChoiceModel(newModels);
    }

    public boolean isNullable() {
        return this.mNullable;
    }

    public void indexTokens(List tokens) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; ++i) {
            this.mSubModels[i].indexTokens(tokens);
        }
    }

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

    public void calcFollowPos(BitSet[] followPosSets) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; ++i) {
            this.mSubModels[i].calcFollowPos(followPosSets);
        }
    }
}

