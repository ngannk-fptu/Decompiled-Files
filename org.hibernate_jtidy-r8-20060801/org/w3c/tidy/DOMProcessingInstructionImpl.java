/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.Node;

public class DOMProcessingInstructionImpl
extends DOMNodeImpl
implements ProcessingInstruction {
    protected DOMProcessingInstructionImpl(Node adaptee) {
        super(adaptee);
    }

    public short getNodeType() {
        return 7;
    }

    public String getTarget() {
        return null;
    }

    public String getData() {
        return this.getNodeValue();
    }

    public void setData(String data) throws DOMException {
        throw new DOMException(7, "Node is read only");
    }
}

