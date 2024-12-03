/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTThickness;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTSurface
extends XmlObject {
    public static final DocumentFactory<CTSurface> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsurface5a19type");
    public static final SchemaType type = Factory.getType();

    public CTThickness getThickness();

    public boolean isSetThickness();

    public void setThickness(CTThickness var1);

    public CTThickness addNewThickness();

    public void unsetThickness();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTPictureOptions getPictureOptions();

    public boolean isSetPictureOptions();

    public void setPictureOptions(CTPictureOptions var1);

    public CTPictureOptions addNewPictureOptions();

    public void unsetPictureOptions();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

