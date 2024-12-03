/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DFAState;
import com.ctc.wstx.dtd.StructValidator;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.StringUtil;
import java.util.TreeSet;

public final class DFAValidator
extends StructValidator {
    DFAState mState;

    public DFAValidator(DFAState initialState) {
        this.mState = initialState;
    }

    public StructValidator newInstance() {
        return new DFAValidator(this.mState);
    }

    public String tryToValidate(PrefixedName elemName) {
        DFAState next = this.mState.findNext(elemName);
        if (next == null) {
            TreeSet names = this.mState.getNextNames();
            if (names.size() == 0) {
                return "Expected $END";
            }
            if (this.mState.isAcceptingState()) {
                return "Expected <" + StringUtil.concatEntries(names, ">, <", null) + "> or $END";
            }
            return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
        }
        this.mState = next;
        return null;
    }

    public String fullyValid() {
        if (this.mState.isAcceptingState()) {
            return null;
        }
        TreeSet names = this.mState.getNextNames();
        return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
    }
}

