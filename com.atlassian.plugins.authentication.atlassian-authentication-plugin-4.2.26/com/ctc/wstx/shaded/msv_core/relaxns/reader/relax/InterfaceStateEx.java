/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InterfaceState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.RELAXCoreIslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

class InterfaceStateEx
extends InterfaceState {
    InterfaceStateEx() {
    }

    protected State createChildState(StartTagInfo tag) {
        String role;
        RELAXCoreIslandSchemaReader reader = (RELAXCoreIslandSchemaReader)this.reader;
        if (!tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
            return null;
        }
        if (tag.localName.equals("div")) {
            return new InterfaceStateEx();
        }
        RELAXModule module = reader.getModule();
        if (tag.localName.equals("export") && (role = tag.getAttribute("role")) != null) {
            module.attPools.getOrCreate((String)role).exported = true;
            return new ChildlessState();
        }
        if (tag.localName.equals("hedgeExport")) {
            String label = tag.getAttribute("label");
            if (label == null) {
                reader.reportError("GrammarReader.MissingAttribute", (Object)"hedgeExport", (Object)"label");
            } else {
                module.hedgeRules.getOrCreate((String)label).exported = true;
            }
            return new ChildlessState();
        }
        return super.createChildState(tag);
    }
}

