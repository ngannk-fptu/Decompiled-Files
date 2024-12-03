/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithoutChildState;

public class NameClassNameState
extends NameClassWithoutChildState {
    protected final StringBuffer text = new StringBuffer();

    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    protected NameClass makeNameClass() {
        String name = WhiteSpaceProcessor.collapse(new String(this.text));
        int idx = name.indexOf(58);
        if (idx < 0) {
            return new SimpleNameClass(this.getPropagatedNamespace(), name);
        }
        String[] qname = this.reader.splitQName(name);
        return new SimpleNameClass(qname[0], qname[1]);
    }
}

