/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.HashSet;
import java.util.Set;

public class IncludeMergeState
extends com.ctc.wstx.shaded.msv_core.reader.trex.IncludeMergeState
implements ExpressionOwner {
    private final Set redefinedPatterns = new HashSet();

    protected State createChildState(StartTagInfo tag) {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        if (tag.localName.equals("define")) {
            return reader.getStateFactory().redefine(this, tag);
        }
        if (tag.localName.equals("start")) {
            return reader.getStateFactory().redefineStart(this, tag);
        }
        return null;
    }

    public void onEndChild(Expression child) {
        if (!(child instanceof ReferenceExp)) {
            return;
        }
        this.redefinedPatterns.add(child);
    }

    public void endSelf() {
        RELAXNGReader.RefExpParseInfo info;
        int i;
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        ReferenceExp[] patterns = this.redefinedPatterns.toArray(new ReferenceExp[0]);
        RELAXNGReader.RefExpParseInfo[] old = new RELAXNGReader.RefExpParseInfo[patterns.length];
        for (i = 0; i < patterns.length; ++i) {
            info = reader.getRefExpParseInfo(patterns[i]);
            old[i] = new RELAXNGReader.RefExpParseInfo();
            old[i].set(info);
            info.haveHead = false;
            info.combineMethod = null;
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalNotFoundYet;
        }
        super.endSelf();
        for (i = 0; i < patterns.length; ++i) {
            info = reader.getRefExpParseInfo(patterns[i]);
            if (info.redefinition == RELAXNGReader.RefExpParseInfo.originalNotFoundYet) {
                reader.reportError("RELAXNGReader.RedefiningUndefined", (Object)patterns[i].name);
            }
            reader.getRefExpParseInfo(patterns[i]).set(old[i]);
        }
    }
}

