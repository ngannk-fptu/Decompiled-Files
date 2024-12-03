/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.TokenModel;
import java.util.BitSet;
import java.util.List;

public class OptionalModel
extends ModelNode {
    ModelNode mModel;

    public OptionalModel(ModelNode model) {
        this.mModel = model;
    }

    @Override
    public ModelNode cloneModel() {
        return new OptionalModel(this.mModel.cloneModel());
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void indexTokens(List<TokenModel> tokens) {
        this.mModel.indexTokens(tokens);
    }

    @Override
    public void addFirstPos(BitSet pos) {
        this.mModel.addFirstPos(pos);
    }

    @Override
    public void addLastPos(BitSet pos) {
        this.mModel.addLastPos(pos);
    }

    @Override
    public void calcFollowPos(BitSet[] followPosSets) {
        this.mModel.calcFollowPos(followPosSets);
    }

    public String toString() {
        return this.mModel + "[?]";
    }
}

