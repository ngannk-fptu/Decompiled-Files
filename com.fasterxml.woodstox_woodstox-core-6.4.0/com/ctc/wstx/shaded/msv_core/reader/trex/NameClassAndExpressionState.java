/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassOwner;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class NameClassAndExpressionState
extends SequenceState
implements NameClassOwner {
    protected NameClass nameClass = null;

    protected String getNamespace() {
        return ((TREXBaseReader)this.reader).targetNamespace;
    }

    protected void startSelf() {
        super.startSelf();
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            return;
        }
        int idx = name.indexOf(58);
        if (idx != -1) {
            String[] s = this.reader.splitQName(name);
            if (s == null) {
                this.reader.reportError("TREXGrammarReader.UndeclaredPrefix", (Object)name);
                this.nameClass = new SimpleNameClass("", name);
            } else {
                this.nameClass = new SimpleNameClass(s[0], s[1]);
            }
        } else {
            this.nameClass = new SimpleNameClass(this.getNamespace(), name);
        }
    }

    public void onEndChild(NameClass p) {
        if (this.nameClass != null) {
            this.reader.reportError("TREXGrammarReader.MoreThanOneNameClass");
        }
        this.nameClass = p;
    }

    protected State createChildState(StartTagInfo tag) {
        if (this.nameClass == null) {
            State nextState = ((TREXBaseReader)this.reader).createNameClassChildState(this, tag);
            if (nextState != null) {
                return nextState;
            }
            nextState = this.reader.createExpressionChildState(this, tag);
            if (nextState != null) {
                this.reader.reportError("TREXGrammarReader.MissingChildNameClass");
                this.nameClass = NameClass.ALL;
                return nextState;
            }
            return null;
        }
        return this.reader.createExpressionChildState(this, tag);
    }

    protected void endSelf() {
        if (this.nameClass == null) {
            this.reader.reportError("TREXGrammarReader.MissingChildNameClass");
            this.nameClass = NameClass.ALL;
        }
        super.endSelf();
    }
}

