/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotSource
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTProtection
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTStyle
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTTextLanguageID
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExternalData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTProtection;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRelId;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTextLanguageID;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTChartSpace
extends XmlObject {
    public static final DocumentFactory<CTChartSpace> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctchartspacef9b4type");
    public static final SchemaType type = Factory.getType();

    public CTBoolean getDate1904();

    public boolean isSetDate1904();

    public void setDate1904(CTBoolean var1);

    public CTBoolean addNewDate1904();

    public void unsetDate1904();

    public CTTextLanguageID getLang();

    public boolean isSetLang();

    public void setLang(CTTextLanguageID var1);

    public CTTextLanguageID addNewLang();

    public void unsetLang();

    public CTBoolean getRoundedCorners();

    public boolean isSetRoundedCorners();

    public void setRoundedCorners(CTBoolean var1);

    public CTBoolean addNewRoundedCorners();

    public void unsetRoundedCorners();

    public CTStyle getStyle();

    public boolean isSetStyle();

    public void setStyle(CTStyle var1);

    public CTStyle addNewStyle();

    public void unsetStyle();

    public CTColorMapping getClrMapOvr();

    public boolean isSetClrMapOvr();

    public void setClrMapOvr(CTColorMapping var1);

    public CTColorMapping addNewClrMapOvr();

    public void unsetClrMapOvr();

    public CTPivotSource getPivotSource();

    public boolean isSetPivotSource();

    public void setPivotSource(CTPivotSource var1);

    public CTPivotSource addNewPivotSource();

    public void unsetPivotSource();

    public CTProtection getProtection();

    public boolean isSetProtection();

    public void setProtection(CTProtection var1);

    public CTProtection addNewProtection();

    public void unsetProtection();

    public CTChart getChart();

    public void setChart(CTChart var1);

    public CTChart addNewChart();

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

    public CTExternalData getExternalData();

    public boolean isSetExternalData();

    public void setExternalData(CTExternalData var1);

    public CTExternalData addNewExternalData();

    public void unsetExternalData();

    public CTPrintSettings getPrintSettings();

    public boolean isSetPrintSettings();

    public void setPrintSettings(CTPrintSettings var1);

    public CTPrintSettings addNewPrintSettings();

    public void unsetPrintSettings();

    public CTRelId getUserShapes();

    public boolean isSetUserShapes();

    public void setUserShapes(CTRelId var1);

    public CTRelId addNewUserShapes();

    public void unsetUserShapes();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

