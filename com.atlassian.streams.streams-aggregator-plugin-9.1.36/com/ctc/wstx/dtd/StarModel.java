/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ModelNode;
import java.util.BitSet;
import java.util.List;

public class StarModel
extends ModelNode {
    ModelNode mModel;

    public StarModel(ModelNode model) {
        this.mModel = model;
    }

    public ModelNode cloneModel() {
        return new StarModel(this.mModel.cloneModel());
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
        BitSet foll = new BitSet();
        this.mModel.addFirstPos(foll);
        BitSet toAddTo = new BitSet();
        this.mModel.addLastPos(toAddTo);
        int ix = 0;
        while ((ix = toAddTo.nextSetBit(ix + 1)) >= 0) {
            followPosSets[ix].or(foll);
        }
    }

    public String toString() {
        return this.mModel.toString() + "*";
    }
}

