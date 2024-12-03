/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleScale
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTSizeRepresents
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleScale;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSizeRepresents;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTBubbleChart
extends XmlObject {
    public static final DocumentFactory<CTBubbleChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbubblechart3ff4type");
    public static final SchemaType type = Factory.getType();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTBubbleSer> getSerList();

    public CTBubbleSer[] getSerArray();

    public CTBubbleSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTBubbleSer[] var1);

    public void setSerArray(int var1, CTBubbleSer var2);

    public CTBubbleSer insertNewSer(int var1);

    public CTBubbleSer addNewSer();

    public void removeSer(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public CTBoolean getBubble3D();

    public boolean isSetBubble3D();

    public void setBubble3D(CTBoolean var1);

    public CTBoolean addNewBubble3D();

    public void unsetBubble3D();

    public CTBubbleScale getBubbleScale();

    public boolean isSetBubbleScale();

    public void setBubbleScale(CTBubbleScale var1);

    public CTBubbleScale addNewBubbleScale();

    public void unsetBubbleScale();

    public CTBoolean getShowNegBubbles();

    public boolean isSetShowNegBubbles();

    public void setShowNegBubbles(CTBoolean var1);

    public CTBoolean addNewShowNegBubbles();

    public void unsetShowNegBubbles();

    public CTSizeRepresents getSizeRepresents();

    public boolean isSetSizeRepresents();

    public void setSizeRepresents(CTSizeRepresents var1);

    public CTSizeRepresents addNewSizeRepresents();

    public void unsetSizeRepresents();

    public List<CTUnsignedInt> getAxIdList();

    public CTUnsignedInt[] getAxIdArray();

    public CTUnsignedInt getAxIdArray(int var1);

    public int sizeOfAxIdArray();

    public void setAxIdArray(CTUnsignedInt[] var1);

    public void setAxIdArray(int var1, CTUnsignedInt var2);

    public CTUnsignedInt insertNewAxId(int var1);

    public CTUnsignedInt addNewAxId();

    public void removeAxId(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

