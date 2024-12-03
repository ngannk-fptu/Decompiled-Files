/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTScatterSer
extends XmlObject {
    public static final DocumentFactory<CTScatterSer> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctscatterser2f7atype");
    public static final SchemaType type = Factory.getType();

    public CTUnsignedInt getIdx();

    public void setIdx(CTUnsignedInt var1);

    public CTUnsignedInt addNewIdx();

    public CTUnsignedInt getOrder();

    public void setOrder(CTUnsignedInt var1);

    public CTUnsignedInt addNewOrder();

    public CTSerTx getTx();

    public boolean isSetTx();

    public void setTx(CTSerTx var1);

    public CTSerTx addNewTx();

    public void unsetTx();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTMarker getMarker();

    public boolean isSetMarker();

    public void setMarker(CTMarker var1);

    public CTMarker addNewMarker();

    public void unsetMarker();

    public List<CTDPt> getDPtList();

    public CTDPt[] getDPtArray();

    public CTDPt getDPtArray(int var1);

    public int sizeOfDPtArray();

    public void setDPtArray(CTDPt[] var1);

    public void setDPtArray(int var1, CTDPt var2);

    public CTDPt insertNewDPt(int var1);

    public CTDPt addNewDPt();

    public void removeDPt(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public List<CTTrendline> getTrendlineList();

    public CTTrendline[] getTrendlineArray();

    public CTTrendline getTrendlineArray(int var1);

    public int sizeOfTrendlineArray();

    public void setTrendlineArray(CTTrendline[] var1);

    public void setTrendlineArray(int var1, CTTrendline var2);

    public CTTrendline insertNewTrendline(int var1);

    public CTTrendline addNewTrendline();

    public void removeTrendline(int var1);

    public List<CTErrBars> getErrBarsList();

    public CTErrBars[] getErrBarsArray();

    public CTErrBars getErrBarsArray(int var1);

    public int sizeOfErrBarsArray();

    public void setErrBarsArray(CTErrBars[] var1);

    public void setErrBarsArray(int var1, CTErrBars var2);

    public CTErrBars insertNewErrBars(int var1);

    public CTErrBars addNewErrBars();

    public void removeErrBars(int var1);

    public CTAxDataSource getXVal();

    public boolean isSetXVal();

    public void setXVal(CTAxDataSource var1);

    public CTAxDataSource addNewXVal();

    public void unsetXVal();

    public CTNumDataSource getYVal();

    public boolean isSetYVal();

    public void setYVal(CTNumDataSource var1);

    public CTNumDataSource addNewYVal();

    public void unsetYVal();

    public CTBoolean getSmooth();

    public boolean isSetSmooth();

    public void setSmooth(CTBoolean var1);

    public CTBoolean addNewSmooth();

    public void unsetSmooth();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

