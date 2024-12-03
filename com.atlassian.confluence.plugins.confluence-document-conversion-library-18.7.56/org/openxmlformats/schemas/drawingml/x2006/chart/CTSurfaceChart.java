/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTBandFmts
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBandFmts;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTSurfaceChart
extends XmlObject {
    public static final DocumentFactory<CTSurfaceChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsurfacecharta96btype");
    public static final SchemaType type = Factory.getType();

    public CTBoolean getWireframe();

    public boolean isSetWireframe();

    public void setWireframe(CTBoolean var1);

    public CTBoolean addNewWireframe();

    public void unsetWireframe();

    public List<CTSurfaceSer> getSerList();

    public CTSurfaceSer[] getSerArray();

    public CTSurfaceSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTSurfaceSer[] var1);

    public void setSerArray(int var1, CTSurfaceSer var2);

    public CTSurfaceSer insertNewSer(int var1);

    public CTSurfaceSer addNewSer();

    public void removeSer(int var1);

    public CTBandFmts getBandFmts();

    public boolean isSetBandFmts();

    public void setBandFmts(CTBandFmts var1);

    public CTBandFmts addNewBandFmts();

    public void unsetBandFmts();

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

