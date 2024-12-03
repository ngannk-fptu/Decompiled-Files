/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.relax.core.DivInModuleState;

public class ModuleMergeState
extends DivInModuleState {
    protected final String expectedTargetNamespace;
    protected String targetNamespace;

    protected ModuleMergeState(String expectedTargetNamespace) {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }

    protected void startSelf() {
        super.startSelf();
        String coreVersion = this.startTag.getAttribute("relaxCoreVersion");
        if (coreVersion == null) {
            this.reader.reportWarning("GrammarReader.MissingAttribute", "module", "relaxCoreVersion");
        } else if (!"1.0".equals(coreVersion)) {
            this.reader.reportWarning("RELAXReader.Warning.IllegalRelaxCoreVersion", coreVersion);
        }
        this.targetNamespace = this.startTag.getAttribute("targetNamespace");
        if (this.targetNamespace != null) {
            if (this.expectedTargetNamespace != null && !this.expectedTargetNamespace.equals(this.targetNamespace)) {
                this.reader.reportError("RELAXReader.InconsistentTargetNamespace", (Object)this.targetNamespace, (Object)this.expectedTargetNamespace);
                this.targetNamespace = this.expectedTargetNamespace;
            }
        } else if (this.expectedTargetNamespace == null) {
            this.reader.reportError("RELAXReader.MissingTargetNamespace");
            this.targetNamespace = "";
        } else {
            this.targetNamespace = this.expectedTargetNamespace;
        }
    }
}

