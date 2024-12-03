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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTDPt
extends XmlObject {
    public static final DocumentFactory<CTDPt> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdpt255etype");
    public static final SchemaType type = Factory.getType();

    public CTUnsignedInt getIdx();

    public void setIdx(CTUnsignedInt var1);

    public CTUnsignedInt addNewIdx();

    public CTBoolean getInvertIfNegative();

    public boolean isSetInvertIfNegative();

    public void setInvertIfNegative(CTBoolean var1);

    public CTBoolean addNewInvertIfNegative();

    public void unsetInvertIfNegative();

    public CTMarker getMarker();

    public boolean isSetMarker();

    public void setMarker(CTMarker var1);

    public CTMarker addNewMarker();

    public void unsetMarker();

    public CTBoolean getBubble3D();

    public boolean isSetBubble3D();

    public void setBubble3D(CTBoolean var1);

    public CTBoolean addNewBubble3D();

    public void unsetBubble3D();

    public CTUnsignedInt getExplosion();

    public boolean isSetExplosion();

    public void setExplosion(CTUnsignedInt var1);

    public CTUnsignedInt addNewExplosion();

    public void unsetExplosion();

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

