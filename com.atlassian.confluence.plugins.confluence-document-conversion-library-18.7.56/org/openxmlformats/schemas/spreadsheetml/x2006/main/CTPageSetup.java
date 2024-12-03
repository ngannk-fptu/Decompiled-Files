/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPositiveUniversalMeasure;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellComments;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPageOrder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPrintError;

public interface CTPageSetup
extends XmlObject {
    public static final DocumentFactory<CTPageSetup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagesetup534dtype");
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

    public long getScale();

    public XmlUnsignedInt xgetScale();

    public boolean isSetScale();

    public void setScale(long var1);

    public void xsetScale(XmlUnsignedInt var1);

    public void unsetScale();

    public long getFirstPageNumber();

    public XmlUnsignedInt xgetFirstPageNumber();

    public boolean isSetFirstPageNumber();

    public void setFirstPageNumber(long var1);

    public void xsetFirstPageNumber(XmlUnsignedInt var1);

    public void unsetFirstPageNumber();

    public long getFitToWidth();

    public XmlUnsignedInt xgetFitToWidth();

    public boolean isSetFitToWidth();

    public void setFitToWidth(long var1);

    public void xsetFitToWidth(XmlUnsignedInt var1);

    public void unsetFitToWidth();

    public long getFitToHeight();

    public XmlUnsignedInt xgetFitToHeight();

    public boolean isSetFitToHeight();

    public void setFitToHeight(long var1);

    public void xsetFitToHeight(XmlUnsignedInt var1);

    public void unsetFitToHeight();

    public STPageOrder.Enum getPageOrder();

    public STPageOrder xgetPageOrder();

    public boolean isSetPageOrder();

    public void setPageOrder(STPageOrder.Enum var1);

    public void xsetPageOrder(STPageOrder var1);

    public void unsetPageOrder();

    public STOrientation.Enum getOrientation();

    public STOrientation xgetOrientation();

    public boolean isSetOrientation();

    public void setOrientation(STOrientation.Enum var1);

    public void xsetOrientation(STOrientation var1);

    public void unsetOrientation();

    public boolean getUsePrinterDefaults();

    public XmlBoolean xgetUsePrinterDefaults();

    public boolean isSetUsePrinterDefaults();

    public void setUsePrinterDefaults(boolean var1);

    public void xsetUsePrinterDefaults(XmlBoolean var1);

    public void unsetUsePrinterDefaults();

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

    public STCellComments.Enum getCellComments();

    public STCellComments xgetCellComments();

    public boolean isSetCellComments();

    public void setCellComments(STCellComments.Enum var1);

    public void xsetCellComments(STCellComments var1);

    public void unsetCellComments();

    public boolean getUseFirstPageNumber();

    public XmlBoolean xgetUseFirstPageNumber();

    public boolean isSetUseFirstPageNumber();

    public void setUseFirstPageNumber(boolean var1);

    public void xsetUseFirstPageNumber(XmlBoolean var1);

    public void unsetUseFirstPageNumber();

    public STPrintError.Enum getErrors();

    public STPrintError xgetErrors();

    public boolean isSetErrors();

    public void setErrors(STPrintError.Enum var1);

    public void xsetErrors(STPrintError var1);

    public void unsetErrors();

    public long getHorizontalDpi();

    public XmlUnsignedInt xgetHorizontalDpi();

    public boolean isSetHorizontalDpi();

    public void setHorizontalDpi(long var1);

    public void xsetHorizontalDpi(XmlUnsignedInt var1);

    public void unsetHorizontalDpi();

    public long getVerticalDpi();

    public XmlUnsignedInt xgetVerticalDpi();

    public boolean isSetVerticalDpi();

    public void setVerticalDpi(long var1);

    public void xsetVerticalDpi(XmlUnsignedInt var1);

    public void unsetVerticalDpi();

    public long getCopies();

    public XmlUnsignedInt xgetCopies();

    public boolean isSetCopies();

    public void setCopies(long var1);

    public void xsetCopies(XmlUnsignedInt var1);

    public void unsetCopies();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();
}

