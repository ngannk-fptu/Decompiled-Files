/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.llom.OMLeafNode;

public class OMCommentImpl
extends OMLeafNode
implements OMComment {
    protected String value;

    public OMCommentImpl(OMContainer parentNode, String contentText, OMFactory factory, boolean fromBuilder) {
        super(parentNode, factory, fromBuilder);
        this.value = contentText;
    }

    public final int getType() {
        return 5;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        writer.writeComment(this.value);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String text) {
        this.value = text;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return this.factory.createOMComment(targetParent, this.value);
    }
}

