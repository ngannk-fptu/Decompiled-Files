/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPictureLocking;

public interface CTNonVisualPictureProperties
extends XmlObject {
    public static final DocumentFactory<CTNonVisualPictureProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnonvisualpicturepropertiesee3ftype");
    public static final SchemaType type = Factory.getType();

    public CTPictureLocking getPicLocks();

    public boolean isSetPicLocks();

    public void setPicLocks(CTPictureLocking var1);

    public CTPictureLocking addNewPicLocks();

    public void unsetPicLocks();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getPreferRelativeResize();

    public XmlBoolean xgetPreferRelativeResize();

    public boolean isSetPreferRelativeResize();

    public void setPreferRelativeResize(boolean var1);

    public void xsetPreferRelativeResize(XmlBoolean var1);

    public void unsetPreferRelativeResize();
}

