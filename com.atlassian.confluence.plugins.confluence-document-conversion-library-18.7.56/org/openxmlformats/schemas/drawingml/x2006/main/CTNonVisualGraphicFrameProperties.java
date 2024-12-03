/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectFrameLocking;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTNonVisualGraphicFrameProperties
extends XmlObject {
    public static final DocumentFactory<CTNonVisualGraphicFrameProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualgraphicframeproperties43b6type");
    public static final SchemaType type = Factory.getType();

    public CTGraphicalObjectFrameLocking getGraphicFrameLocks();

    public boolean isSetGraphicFrameLocks();

    public void setGraphicFrameLocks(CTGraphicalObjectFrameLocking var1);

    public CTGraphicalObjectFrameLocking addNewGraphicFrameLocks();

    public void unsetGraphicFrameLocks();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}

