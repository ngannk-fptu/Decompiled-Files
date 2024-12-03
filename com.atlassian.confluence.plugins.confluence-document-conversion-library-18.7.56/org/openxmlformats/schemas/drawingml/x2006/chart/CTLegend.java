/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendEntry;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendPos;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTLegend
extends XmlObject {
    public static final DocumentFactory<CTLegend> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlegenda54ftype");
    public static final SchemaType type = Factory.getType();

    public CTLegendPos getLegendPos();

    public boolean isSetLegendPos();

    public void setLegendPos(CTLegendPos var1);

    public CTLegendPos addNewLegendPos();

    public void unsetLegendPos();

    public List<CTLegendEntry> getLegendEntryList();

    public CTLegendEntry[] getLegendEntryArray();

    public CTLegendEntry getLegendEntryArray(int var1);

    public int sizeOfLegendEntryArray();

    public void setLegendEntryArray(CTLegendEntry[] var1);

    public void setLegendEntryArray(int var1, CTLegendEntry var2);

    public CTLegendEntry insertNewLegendEntry(int var1);

    public CTLegendEntry addNewLegendEntry();

    public void removeLegendEntry(int var1);

    public CTLayout getLayout();

    public boolean isSetLayout();

    public void setLayout(CTLayout var1);

    public CTLayout addNewLayout();

    public void unsetLayout();

    public CTBoolean getOverlay();

    public boolean isSetOverlay();

    public void setOverlay(CTBoolean var1);

    public CTBoolean addNewOverlay();

    public void unsetOverlay();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTTextBody getTxPr();

    public boolean isSetTxPr();

    public void setTxPr(CTTextBody var1);

    public CTTextBody addNewTxPr();

    public void unsetTxPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

