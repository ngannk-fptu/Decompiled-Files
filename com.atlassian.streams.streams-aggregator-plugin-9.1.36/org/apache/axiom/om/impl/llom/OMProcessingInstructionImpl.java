/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.impl.llom.OMLeafNode;

public class OMProcessingInstructionImpl
extends OMLeafNode
implements OMProcessingInstruction {
    protected String target;
    protected String value;

    public OMProcessingInstructionImpl(OMContainer parentNode, String target, String value, OMFactory factory, boolean fromBuilder) {
        super(parentNode, factory, fromBuilder);
        this.target = target;
        this.value = value;
    }

    public final int getType() {
        return 3;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        writer.writeProcessingInstruction(this.target + " ", this.value);
    }

    public String getValue() {
        return this.value;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setValue(String text) {
        this.value = text;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return this.factory.createOMProcessingInstruction(targetParent, this.target, this.value);
    }
}

