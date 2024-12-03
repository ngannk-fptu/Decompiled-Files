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

public interface CTPictureLocking
extends XmlObject {
    public static final DocumentFactory<CTPictureLocking> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpicturelockinga414type");
    public static final SchemaType type = Factory.getType();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getNoGrp();

    public XmlBoolean xgetNoGrp();

    public boolean isSetNoGrp();

    public void setNoGrp(boolean var1);

    public void xsetNoGrp(XmlBoolean var1);

    public void unsetNoGrp();

    public boolean getNoSelect();

    public XmlBoolean xgetNoSelect();

    public boolean isSetNoSelect();

    public void setNoSelect(boolean var1);

    public void xsetNoSelect(XmlBoolean var1);

    public void unsetNoSelect();

    public boolean getNoRot();

    public XmlBoolean xgetNoRot();

    public boolean isSetNoRot();

    public void setNoRot(boolean var1);

    public void xsetNoRot(XmlBoolean var1);

    public void unsetNoRot();

    public boolean getNoChangeAspect();

    public XmlBoolean xgetNoChangeAspect();

    public boolean isSetNoChangeAspect();

    public void setNoChangeAspect(boolean var1);

    public void xsetNoChangeAspect(XmlBoolean var1);

    public void unsetNoChangeAspect();

    public boolean getNoMove();

    public XmlBoolean xgetNoMove();

    public boolean isSetNoMove();

    public void setNoMove(boolean var1);

    public void xsetNoMove(XmlBoolean var1);

    public void unsetNoMove();

    public boolean getNoResize();

    public XmlBoolean xgetNoResize();

    public boolean isSetNoResize();

    public void setNoResize(boolean var1);

    public void xsetNoResize(XmlBoolean var1);

    public void unsetNoResize();

    public boolean getNoEditPoints();

    public XmlBoolean xgetNoEditPoints();

    public boolean isSetNoEditPoints();

    public void setNoEditPoints(boolean var1);

    public void xsetNoEditPoints(XmlBoolean var1);

    public void unsetNoEditPoints();

    public boolean getNoAdjustHandles();

    public XmlBoolean xgetNoAdjustHandles();

    public boolean isSetNoAdjustHandles();

    public void setNoAdjustHandles(boolean var1);

    public void xsetNoAdjustHandles(XmlBoolean var1);

    public void unsetNoAdjustHandles();

    public boolean getNoChangeArrowheads();

    public XmlBoolean xgetNoChangeArrowheads();

    public boolean isSetNoChangeArrowheads();

    public void setNoChangeArrowheads(boolean var1);

    public void xsetNoChangeArrowheads(XmlBoolean var1);

    public void unsetNoChangeArrowheads();

    public boolean getNoChangeShapeType();

    public XmlBoolean xgetNoChangeShapeType();

    public boolean isSetNoChangeShapeType();

    public void setNoChangeShapeType(boolean var1);

    public void xsetNoChangeShapeType(XmlBoolean var1);

    public void unsetNoChangeShapeType();

    public boolean getNoCrop();

    public XmlBoolean xgetNoCrop();

    public boolean isSetNoCrop();

    public void setNoCrop(boolean var1);

    public void xsetNoCrop(XmlBoolean var1);

    public void unsetNoCrop();
}

