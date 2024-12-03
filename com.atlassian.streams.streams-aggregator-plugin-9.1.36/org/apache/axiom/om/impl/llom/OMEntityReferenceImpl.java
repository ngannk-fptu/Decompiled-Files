/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.llom.OMLeafNode;

public class OMEntityReferenceImpl
extends OMLeafNode
implements OMEntityReference {
    private final String name;
    private final String replacementText;

    public OMEntityReferenceImpl(OMContainer parent, String name, String replacementText, OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.name = name;
        this.replacementText = replacementText;
    }

    public int getType() {
        return 9;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        writer.writeEntityRef(this.name);
    }

    public String getName() {
        return this.name;
    }

    public String getReplacementText() {
        return this.replacementText;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return new OMEntityReferenceImpl(targetParent, this.name, this.replacementText, this.factory, false);
    }
}

