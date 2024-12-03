/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CharacterDataImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl
extends CharacterDataImpl
implements ProcessingInstruction {
    private final String target_;

    public ProcessingInstructionImpl(CoreDocumentImpl ownerDoc, String target, String data) {
        super(ownerDoc, data);
        this.target_ = target;
    }

    @Override
    public short getNodeType() {
        return 7;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target_;
    }

    @Override
    public String getTarget() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target_;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.ownerNode.getBaseURI();
    }
}

