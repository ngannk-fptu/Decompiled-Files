/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGroupLocking
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupLocking;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTNonVisualGroupDrawingShapeProps
extends XmlObject {
    public static final DocumentFactory<CTNonVisualGroupDrawingShapeProps> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualgroupdrawingshapeprops610ctype");
    public static final SchemaType type = Factory.getType();

    public CTGroupLocking getGrpSpLocks();

    public boolean isSetGrpSpLocks();

    public void setGrpSpLocks(CTGroupLocking var1);

    public CTGroupLocking addNewGrpSpLocks();

    public void unsetGrpSpLocks();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}

