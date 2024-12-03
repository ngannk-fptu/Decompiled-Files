/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;

public interface OMNode
extends OMSerializable {
    public static final short ELEMENT_NODE = 1;
    public static final short TEXT_NODE = 4;
    public static final short CDATA_SECTION_NODE = 12;
    public static final short COMMENT_NODE = 5;
    public static final short DTD_NODE = 11;
    public static final short PI_NODE = 3;
    public static final short ENTITY_REFERENCE_NODE = 9;
    public static final short SPACE_NODE = 6;

    public OMContainer getParent();

    public OMNode getNextOMSibling() throws OMException;

    public OMNode detach() throws OMException;

    public void discard() throws OMException;

    public void insertSiblingAfter(OMNode var1) throws OMException;

    public void insertSiblingBefore(OMNode var1) throws OMException;

    public int getType();

    public OMNode getPreviousOMSibling();

    public void serialize(OutputStream var1) throws XMLStreamException;

    public void serialize(Writer var1) throws XMLStreamException;

    public void serialize(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serialize(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1) throws XMLStreamException;

    public void serializeAndConsume(Writer var1) throws XMLStreamException;

    public void serializeAndConsume(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serializeAndConsume(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public void buildWithAttachments();
}

