/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ModuleMergeState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

class RootModuleMergeState
extends SimpleState {
    RootModuleMergeState() {
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.namespaceURI.equals("http://www.xml.gr.jp/xmlns/relaxCore") && tag.localName.equals("module")) {
            return new ModuleMergeState(((RELAXCoreReader)this.reader).module.targetNamespace);
        }
        return null;
    }
}

