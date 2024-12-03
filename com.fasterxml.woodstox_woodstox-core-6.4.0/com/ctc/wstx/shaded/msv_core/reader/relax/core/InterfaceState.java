/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class InterfaceState
extends SimpleState {
    protected State createChildState(StartTagInfo tag) {
        if (!tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
            return null;
        }
        if (tag.localName.equals("div")) {
            return new InterfaceState();
        }
        RELAXModule module = this.getReader().module;
        if (tag.localName.equals("export")) {
            String label = tag.getAttribute("label");
            if (label != null) {
                module.elementRules.getOrCreate((String)label).exported = true;
            } else {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"export", (Object)"label");
            }
            return new ChildlessState();
        }
        return null;
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader)this.reader;
    }
}

