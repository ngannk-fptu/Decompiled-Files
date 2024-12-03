/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl
extends CharacterDataImpl
implements ProcessingInstruction {
    static final long serialVersionUID = 7554435174099981510L;
    protected String target;

    public ProcessingInstructionImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2) {
        super(coreDocumentImpl, string2);
        this.target = string;
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
        return this.target;
    }

    @Override
    public String getTarget() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.ownerNode.getBaseURI();
    }
}

