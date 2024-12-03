/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STTargetScreenSize
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTargetScreenSize;

public interface CTWebPublishing
extends XmlObject {
    public static final DocumentFactory<CTWebPublishing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctwebpublishing4646type");
    public static final SchemaType type = Factory.getType();

    public boolean getCss();

    public XmlBoolean xgetCss();

    public boolean isSetCss();

    public void setCss(boolean var1);

    public void xsetCss(XmlBoolean var1);

    public void unsetCss();

    public boolean getThicket();

    public XmlBoolean xgetThicket();

    public boolean isSetThicket();

    public void setThicket(boolean var1);

    public void xsetThicket(XmlBoolean var1);

    public void unsetThicket();

    public boolean getLongFileNames();

    public XmlBoolean xgetLongFileNames();

    public boolean isSetLongFileNames();

    public void setLongFileNames(boolean var1);

    public void xsetLongFileNames(XmlBoolean var1);

    public void unsetLongFileNames();

    public boolean getVml();

    public XmlBoolean xgetVml();

    public boolean isSetVml();

    public void setVml(boolean var1);

    public void xsetVml(XmlBoolean var1);

    public void unsetVml();

    public boolean getAllowPng();

    public XmlBoolean xgetAllowPng();

    public boolean isSetAllowPng();

    public void setAllowPng(boolean var1);

    public void xsetAllowPng(XmlBoolean var1);

    public void unsetAllowPng();

    public STTargetScreenSize.Enum getTargetScreenSize();

    public STTargetScreenSize xgetTargetScreenSize();

    public boolean isSetTargetScreenSize();

    public void setTargetScreenSize(STTargetScreenSize.Enum var1);

    public void xsetTargetScreenSize(STTargetScreenSize var1);

    public void unsetTargetScreenSize();

    public long getDpi();

    public XmlUnsignedInt xgetDpi();

    public boolean isSetDpi();

    public void setDpi(long var1);

    public void xsetDpi(XmlUnsignedInt var1);

    public void unsetDpi();

    public long getCodePage();

    public XmlUnsignedInt xgetCodePage();

    public boolean isSetCodePage();

    public void setCodePage(long var1);

    public void xsetCodePage(XmlUnsignedInt var1);

    public void unsetCodePage();

    public String getCharacterSet();

    public XmlString xgetCharacterSet();

    public boolean isSetCharacterSet();

    public void setCharacterSet(String var1);

    public void xsetCharacterSet(XmlString var1);

    public void unsetCharacterSet();
}

