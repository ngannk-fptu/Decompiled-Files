/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassOwner;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class NameClassWithChildState
extends NameClassState
implements NameClassOwner {
    protected NameClass nameClass = null;
    protected boolean allowNullChild = false;

    public final void onEndChild(NameClass childNameClass) {
        this.nameClass = this.castNameClass(this.nameClass, childNameClass);
    }

    protected final NameClass makeNameClass() {
        if (this.nameClass == null && !this.allowNullChild) {
            this.reader.reportError("TREXGrammarReader.MissingChildNameClass");
            this.nameClass = NameClass.ALL;
        }
        return this.annealNameClass(this.nameClass);
    }

    protected State createChildState(StartTagInfo tag) {
        return ((TREXBaseReader)this.reader).createNameClassChildState(this, tag);
    }

    protected abstract NameClass castNameClass(NameClass var1, NameClass var2);

    protected NameClass annealNameClass(NameClass nameClass) {
        return nameClass;
    }
}

