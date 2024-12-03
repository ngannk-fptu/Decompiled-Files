/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ModuleState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

class RootModuleState
extends SimpleState {
    protected final String expectedNamespace;

    RootModuleState(String expectedNamespace) {
        this.expectedNamespace = expectedNamespace;
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore") && tag.localName.equals("module")) {
            return new ModuleState(this.expectedNamespace);
        }
        return null;
    }

    protected void endSelf() {
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        reader.wrapUp();
        super.endSelf();
    }
}

