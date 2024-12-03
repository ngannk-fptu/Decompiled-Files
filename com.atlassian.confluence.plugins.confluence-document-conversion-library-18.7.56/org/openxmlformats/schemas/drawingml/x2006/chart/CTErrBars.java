/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBarType;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrValType;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTErrBars
extends XmlObject {
    public static final DocumentFactory<CTErrBars> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cterrbarsa201type");
    public static final SchemaType type = Factory.getType();

    public CTErrDir getErrDir();

    public boolean isSetErrDir();

    public void setErrDir(CTErrDir var1);

    public CTErrDir addNewErrDir();

    public void unsetErrDir();

    public CTErrBarType getErrBarType();

    public void setErrBarType(CTErrBarType var1);

    public CTErrBarType addNewErrBarType();

    public CTErrValType getErrValType();

    public void setErrValType(CTErrValType var1);

    public CTErrValType addNewErrValType();

    public CTBoolean getNoEndCap();

    public boolean isSetNoEndCap();

    public void setNoEndCap(CTBoolean var1);

    public CTBoolean addNewNoEndCap();

    public void unsetNoEndCap();

    public CTNumDataSource getPlus();

    public boolean isSetPlus();

    public void setPlus(CTNumDataSource var1);

    public CTNumDataSource addNewPlus();

    public void unsetPlus();

    public CTNumDataSource getMinus();

    public boolean isSetMinus();

    public void setMinus(CTNumDataSource var1);

    public CTNumDataSource addNewMinus();

    public void unsetMinus();

    public CTDouble getVal();

    public boolean isSetVal();

    public void setVal(CTDouble var1);

    public CTDouble addNewVal();

    public void unsetVal();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

