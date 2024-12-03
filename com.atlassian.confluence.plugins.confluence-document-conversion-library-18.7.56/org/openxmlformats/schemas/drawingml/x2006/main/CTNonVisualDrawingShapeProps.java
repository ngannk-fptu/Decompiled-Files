/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTShapeLocking
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeLocking;

public interface CTNonVisualDrawingShapeProps
extends XmlObject {
    public static final DocumentFactory<CTNonVisualDrawingShapeProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualdrawingshapepropsf17btype");
    public static final SchemaType type = Factory.getType();

    public CTShapeLocking getSpLocks();

    public boolean isSetSpLocks();

    public void setSpLocks(CTShapeLocking var1);

    public CTShapeLocking addNewSpLocks();

    public void unsetSpLocks();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getTxBox();

    public XmlBoolean xgetTxBox();

    public boolean isSetTxBox();

    public void setTxBox(boolean var1);

    public void xsetTxBox(XmlBoolean var1);

    public void unsetTxBox();
}

