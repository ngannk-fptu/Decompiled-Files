/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STPageSetupOrientation
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STPageSetupOrientation;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPositiveUniversalMeasure;

public interface CTPageSetup
extends XmlObject {
    public static final DocumentFactory<CTPageSetup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagesetupdb38type");
    public static final SchemaType type = Factory.getType();

    public long getPaperSize();

    public XmlUnsignedInt xgetPaperSize();

    public boolean isSetPaperSize();

    public void setPaperSize(long var1);

    public void xsetPaperSize(XmlUnsignedInt var1);

    public void unsetPaperSize();

    public String getPaperHeight();

    public STPositiveUniversalMeasure xgetPaperHeight();

    public boolean isSetPaperHeight();

    public void setPaperHeight(String var1);

    public void xsetPaperHeight(STPositiveUniversalMeasure var1);

    public void unsetPaperHeight();

    public String getPaperWidth();

    public STPositiveUniversalMeasure xgetPaperWidth();

    public boolean isSetPaperWidth();

    public void setPaperWidth(String var1);

    public void xsetPaperWidth(STPositiveUniversalMeasure var1);

    public void unsetPaperWidth();

    public long getFirstPageNumber();

    public XmlUnsignedInt xgetFirstPageNumber();

    public boolean isSetFirstPageNumber();

    public void setFirstPageNumber(long var1);

    public void xsetFirstPageNumber(XmlUnsignedInt var1);

    public void unsetFirstPageNumber();

    public STPageSetupOrientation.Enum getOrientation();

    public STPageSetupOrientation xgetOrientation();

    public boolean isSetOrientation();

    public void setOrientation(STPageSetupOrientation.Enum var1);

    public void xsetOrientation(STPageSetupOrientation var1);

    public void unsetOrientation();

    public boolean getBlackAndWhite();

    public XmlBoolean xgetBlackAndWhite();

    public boolean isSetBlackAndWhite();

    public void setBlackAndWhite(boolean var1);

    public void xsetBlackAndWhite(XmlBoolean var1);

    public void unsetBlackAndWhite();

    public boolean getDraft();

    public XmlBoolean xgetDraft();

    public boolean isSetDraft();

    public void setDraft(boolean var1);

    public void xsetDraft(XmlBoolean var1);

    public void unsetDraft();

    public boolean getUseFirstPageNumber();

    public XmlBoolean xgetUseFirstPageNumber();

    public boolean isSetUseFirstPageNumber();

    public void setUseFirstPageNumber(boolean var1);

    public void xsetUseFirstPageNumber(XmlBoolean var1);

    public void unsetUseFirstPageNumber();

    public int getHorizontalDpi();

    public XmlInt xgetHorizontalDpi();

    public boolean isSetHorizontalDpi();

    public void setHorizontalDpi(int var1);

    public void xsetHorizontalDpi(XmlInt var1);

    public void unsetHorizontalDpi();

    public int getVerticalDpi();

    public XmlInt xgetVerticalDpi();

    public boolean isSetVerticalDpi();

    public void setVerticalDpi(int var1);

    public void xsetVerticalDpi(XmlInt var1);

    public void unsetVerticalDpi();

    public long getCopies();

    public XmlUnsignedInt xgetCopies();

    public boolean isSetCopies();

    public void setCopies(long var1);

    public void xsetCopies(XmlUnsignedInt var1);

    public void unsetCopies();
}

