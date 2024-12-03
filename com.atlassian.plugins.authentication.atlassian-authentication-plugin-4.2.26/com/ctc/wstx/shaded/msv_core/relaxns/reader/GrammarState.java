/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.IncludeGrammarState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.NamespaceState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RELAXNSReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.TopLevelState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class GrammarState
extends SimpleState {
    protected RELAXNSReader getReader() {
        return (RELAXNSReader)this.reader;
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("namespace")) {
            return new NamespaceState();
        }
        if (tag.localName.equals("topLevel")) {
            return new TopLevelState();
        }
        if (tag.localName.equals("include")) {
            return new IncludeGrammarState();
        }
        return null;
    }

    protected void startSelf() {
        super.startSelf();
        String nsVersion = this.startTag.getAttribute("relaxNamespaceVersion");
        if (nsVersion == null) {
            this.reader.reportWarning("GrammarReader.MissingAttribute", "module", "relaxNamespaceVersion");
        } else if (!"1.0".equals(nsVersion)) {
            this.reader.reportWarning("RELAXNSReader.Warning.IllegalRelaxNamespaceVersion", nsVersion);
        }
    }
}

