/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;
import com.microsoft.schemas.office.visio.x2012.main.RelType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PageType
extends XmlObject {
    public static final DocumentFactory<PageType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pagetype2fcatype");
    public static final SchemaType type = Factory.getType();

    public PageSheetType getPageSheet();

    public boolean isSetPageSheet();

    public void setPageSheet(PageSheetType var1);

    public PageSheetType addNewPageSheet();

    public void unsetPageSheet();

    public RelType getRel();

    public void setRel(RelType var1);

    public RelType addNewRel();

    public long getID();

    public XmlUnsignedInt xgetID();

    public void setID(long var1);

    public void xsetID(XmlUnsignedInt var1);

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();

    public String getNameU();

    public XmlString xgetNameU();

    public boolean isSetNameU();

    public void setNameU(String var1);

    public void xsetNameU(XmlString var1);

    public void unsetNameU();

    public boolean getIsCustomName();

    public XmlBoolean xgetIsCustomName();

    public boolean isSetIsCustomName();

    public void setIsCustomName(boolean var1);

    public void xsetIsCustomName(XmlBoolean var1);

    public void unsetIsCustomName();

    public boolean getIsCustomNameU();

    public XmlBoolean xgetIsCustomNameU();

    public boolean isSetIsCustomNameU();

    public void setIsCustomNameU(boolean var1);

    public void xsetIsCustomNameU(XmlBoolean var1);

    public void unsetIsCustomNameU();

    public boolean getBackground();

    public XmlBoolean xgetBackground();

    public boolean isSetBackground();

    public void setBackground(boolean var1);

    public void xsetBackground(XmlBoolean var1);

    public void unsetBackground();

    public long getBackPage();

    public XmlUnsignedInt xgetBackPage();

    public boolean isSetBackPage();

    public void setBackPage(long var1);

    public void xsetBackPage(XmlUnsignedInt var1);

    public void unsetBackPage();

    public double getViewScale();

    public XmlDouble xgetViewScale();

    public boolean isSetViewScale();

    public void setViewScale(double var1);

    public void xsetViewScale(XmlDouble var1);

    public void unsetViewScale();

    public double getViewCenterX();

    public XmlDouble xgetViewCenterX();

    public boolean isSetViewCenterX();

    public void setViewCenterX(double var1);

    public void xsetViewCenterX(XmlDouble var1);

    public void unsetViewCenterX();

    public double getViewCenterY();

    public XmlDouble xgetViewCenterY();

    public boolean isSetViewCenterY();

    public void setViewCenterY(double var1);

    public void xsetViewCenterY(XmlDouble var1);

    public void unsetViewCenterY();

    public long getReviewerID();

    public XmlUnsignedInt xgetReviewerID();

    public boolean isSetReviewerID();

    public void setReviewerID(long var1);

    public void xsetReviewerID(XmlUnsignedInt var1);

    public void unsetReviewerID();

    public long getAssociatedPage();

    public XmlUnsignedInt xgetAssociatedPage();

    public boolean isSetAssociatedPage();

    public void setAssociatedPage(long var1);

    public void xsetAssociatedPage(XmlUnsignedInt var1);

    public void unsetAssociatedPage();
}

