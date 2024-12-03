/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLCommonTimeNodeData;

public interface CTTLTimeNodeParallel
extends XmlObject {
    public static final DocumentFactory<CTTLTimeNodeParallel> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttltimenodeparallelf917type");
    public static final SchemaType type = Factory.getType();

    public CTTLCommonTimeNodeData getCTn();

    public void setCTn(CTTLCommonTimeNodeData var1);

    public CTTLCommonTimeNodeData addNewCTn();
}

