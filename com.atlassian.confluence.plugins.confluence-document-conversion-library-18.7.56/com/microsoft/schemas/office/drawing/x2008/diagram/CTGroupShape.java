/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShapeNonVisual;
import com.microsoft.schemas.office.drawing.x2008.diagram.CTShape;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTGroupShape
extends XmlObject {
    public static final DocumentFactory<CTGroupShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroupshape48cbtype");
    public static final SchemaType type = Factory.getType();

    public CTGroupShapeNonVisual getNvGrpSpPr();

    public void setNvGrpSpPr(CTGroupShapeNonVisual var1);

    public CTGroupShapeNonVisual addNewNvGrpSpPr();

    public CTGroupShapeProperties getGrpSpPr();

    public void setGrpSpPr(CTGroupShapeProperties var1);

    public CTGroupShapeProperties addNewGrpSpPr();

    public List<CTShape> getSpList();

    public CTShape[] getSpArray();

    public CTShape getSpArray(int var1);

    public int sizeOfSpArray();

    public void setSpArray(CTShape[] var1);

    public void setSpArray(int var1, CTShape var2);

    public CTShape insertNewSp(int var1);

    public CTShape addNewSp();

    public void removeSp(int var1);

    public List<CTGroupShape> getGrpSpList();

    public CTGroupShape[] getGrpSpArray();

    public CTGroupShape getGrpSpArray(int var1);

    public int sizeOfGrpSpArray();

    public void setGrpSpArray(CTGroupShape[] var1);

    public void setGrpSpArray(int var1, CTGroupShape var2);

    public CTGroupShape insertNewGrpSp(int var1);

    public CTGroupShape addNewGrpSp();

    public void removeGrpSp(int var1);

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}

