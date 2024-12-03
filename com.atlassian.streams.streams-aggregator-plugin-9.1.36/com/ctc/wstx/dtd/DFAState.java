/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.ConcatModel;
import com.ctc.wstx.dtd.ContentSpec;
import com.ctc.wstx.dtd.ModelNode;
import com.ctc.wstx.dtd.TokenModel;
import com.ctc.wstx.util.PrefixedName;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public final class DFAState {
    final int mIndex;
    final boolean mAccepting;
    BitSet mTokenSet;
    HashMap mNext = new HashMap();

    public DFAState(int index, BitSet tokenSet) {
        this.mIndex = index;
        this.mAccepting = tokenSet.get(0);
        this.mTokenSet = tokenSet;
    }

    public static DFAState constructDFA(ContentSpec rootSpec) {
        ModelNode modelRoot = rootSpec.rewrite();
        TokenModel eofToken = TokenModel.getNullToken();
        ConcatModel dummyRoot = new ConcatModel(modelRoot, eofToken);
        ArrayList<TokenModel> tokens = new ArrayList<TokenModel>();
        tokens.add(eofToken);
        dummyRoot.indexTokens(tokens);
        int flen = tokens.size();
        BitSet[] followPos = new BitSet[flen];
        PrefixedName[] tokenNames = new PrefixedName[flen];
        for (int i = 0; i < flen; ++i) {
            followPos[i] = new BitSet(flen);
            tokenNames[i] = ((TokenModel)tokens.get(i)).getName();
        }
        dummyRoot.calcFollowPos(followPos);
        BitSet initial = new BitSet(flen);
        dummyRoot.addFirstPos(initial);
        DFAState firstState = new DFAState(0, initial);
        ArrayList<DFAState> stateList = new ArrayList<DFAState>();
        stateList.add(firstState);
        HashMap<BitSet, DFAState> stateMap = new HashMap<BitSet, DFAState>();
        stateMap.put(initial, firstState);
        int i = 0;
        while (i < stateList.size()) {
            DFAState curr = (DFAState)stateList.get(i++);
            curr.calcNext(tokenNames, followPos, stateList, stateMap);
        }
        return firstState;
    }

    public boolean isAcceptingState() {
        return this.mAccepting;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public DFAState findNext(PrefixedName elemName) {
        return (DFAState)this.mNext.get(elemName);
    }

    public TreeSet getNextNames() {
        TreeSet names = new TreeSet();
        Iterator it = this.mNext.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            names.add(o);
        }
        return names;
    }

    public void calcNext(PrefixedName[] tokenNames, BitSet[] tokenFPs, List stateList, Map stateMap) {
        int first = -1;
        BitSet tokenSet = (BitSet)this.mTokenSet.clone();
        this.mTokenSet = null;
        while ((first = tokenSet.nextSetBit(first + 1)) >= 0) {
            PrefixedName tokenName = tokenNames[first];
            if (tokenName == null) continue;
            BitSet nextGroup = (BitSet)tokenFPs[first].clone();
            int second = first;
            while ((second = tokenSet.nextSetBit(second + 1)) > 0) {
                if (tokenNames[second] != tokenName) continue;
                tokenSet.clear(second);
                nextGroup.or(tokenFPs[second]);
            }
            DFAState next = (DFAState)stateMap.get(nextGroup);
            if (next == null) {
                next = new DFAState(stateList.size(), nextGroup);
                stateList.add(next);
                stateMap.put(nextGroup, next);
            }
            this.mNext.put(tokenName, next);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("State #" + this.mIndex + ":\n");
        sb.append("  Accepting: " + this.mAccepting);
        sb.append("\n  Next states:\n");
        Iterator it = this.mNext.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry en = it.next();
            sb.append(en.getKey());
            sb.append(" -> ");
            DFAState next = (DFAState)en.getValue();
            sb.append(next.getIndex());
            sb.append("\n");
        }
        return sb.toString();
    }
}

