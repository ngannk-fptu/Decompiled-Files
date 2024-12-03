/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMCloneOptions;

public class SOAPCloneOptions
extends OMCloneOptions {
    private Boolean processedFlag;

    public Boolean getProcessedFlag() {
        return this.processedFlag;
    }

    public void setProcessedFlag(Boolean processedFlag) {
        this.processedFlag = processedFlag;
    }
}

