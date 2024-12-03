/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.llom.OMLeafNode;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

public class OMDocTypeImpl
extends OMLeafNode
implements OMDocType {
    private final String rootName;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;

    public OMDocTypeImpl(OMContainer parentNode, String rootName, String publicId, String systemId, String internalSubset, OMFactory factory, boolean fromBuilder) {
        super(parentNode, factory, fromBuilder);
        this.rootName = rootName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
    }

    public final int getType() {
        return 11;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        XMLStreamWriterUtils.writeDTD(writer, this.rootName, this.publicId, this.systemId, this.internalSubset);
    }

    public String getRootName() {
        return this.rootName;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getInternalSubset() {
        return this.internalSubset;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return this.factory.createOMDocType(targetParent, this.rootName, this.publicId, this.systemId, this.internalSubset);
    }
}

