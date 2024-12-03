/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import java.util.BitSet;
import java.util.List;

public class OptionalModel
extends ModelNode {
    ModelNode mModel;

    public OptionalModel(ModelNode model) {
        this.mModel = model;
    }

    public ModelNode cloneModel() {
        return new OptionalModel(this.mModel.cloneModel());
    }

    public boolean isNullable() {
        return true;
    }

    public void indexTokens(List tokens) {
        this.mModel.indexTokens(tokens);
    }

    public void addFirstPos(BitSet pos) {
        this.mModel.addFirstPos(pos);
    }

    public void addLastPos(BitSet pos) {
        this.mModel.addLastPos(pos);
    }

    public void calcFollowPos(BitSet[] followPosSets) {
        this.mModel.calcFollowPos(followPosSets);
    }

    public String toString() {
        return this.mModel + "[?]";
    }
}

