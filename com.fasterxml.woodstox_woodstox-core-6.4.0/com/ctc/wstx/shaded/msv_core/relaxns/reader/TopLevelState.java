/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.HedgeRuleBaseState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class TopLevelState
extends HedgeRuleBaseState {
    protected void endSelf(Expression contentModel) {
        ((RELAXNSReader)this.reader).grammar.topLevel = contentModel;
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxNamespace")) {
            this.reader.reportError("RELAXNSReader.TopLevelParticleMustBeRelaxCore");
            return null;
        }
        return super.createChildState(tag);
    }

    protected boolean isGrammarElement(StartTagInfo tag) {
        if (tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
            return true;
        }
        return tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxNamespace");
    }
}

