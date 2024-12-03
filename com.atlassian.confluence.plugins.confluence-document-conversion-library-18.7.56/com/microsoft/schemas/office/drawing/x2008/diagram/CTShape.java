/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.diagram.STModelId
 */
package com.microsoft.schemas.office.drawing.x2008.diagram;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTShapeNonVisual;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.diagram.STModelId;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;

public interface CTShape
extends XmlObject {
    public static final DocumentFactory<CTShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshape6416type");
    public static final SchemaType type = Factory.getType();

    public CTShapeNonVisual getNvSpPr();

    public void setNvSpPr(CTShapeNonVisual var1);

    public CTShapeNonVisual addNewNvSpPr();

    public CTShapeProperties getSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public CTShapeStyle getStyle();

    public boolean isSetStyle();

    public void setStyle(CTShapeStyle var1);

    public CTShapeStyle addNewStyle();

    public void unsetStyle();

    public CTTextBody getTxBody();

    public boolean isSetTxBody();

    public void setTxBody(CTTextBody var1);

    public CTTextBody addNewTxBody();

    public void unsetTxBody();

    public CTTransform2D getTxXfrm();

    public boolean isSetTxXfrm();

    public void setTxXfrm(CTTransform2D var1);

    public CTTransform2D addNewTxXfrm();

    public void unsetTxXfrm();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getModelId();

    public STModelId xgetModelId();

    public void setModelId(Object var1);

    public void xsetModelId(STModelId var1);
}

