/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public interface CTH
extends XmlObject {
    public static final DocumentFactory<CTH> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cth4cbctype");
    public static final SchemaType type = Factory.getType();

    public String getPosition();

    public XmlString xgetPosition();

    public boolean isSetPosition();

    public void setPosition(String var1);

    public void xsetPosition(XmlString var1);

    public void unsetPosition();

    public String getPolar();

    public XmlString xgetPolar();

    public boolean isSetPolar();

    public void setPolar(String var1);

    public void xsetPolar(XmlString var1);

    public void unsetPolar();

    public String getMap();

    public XmlString xgetMap();

    public boolean isSetMap();

    public void setMap(String var1);

    public void xsetMap(XmlString var1);

    public void unsetMap();

    public STTrueFalse.Enum getInvx();

    public STTrueFalse xgetInvx();

    public boolean isSetInvx();

    public void setInvx(STTrueFalse.Enum var1);

    public void xsetInvx(STTrueFalse var1);

    public void unsetInvx();

    public STTrueFalse.Enum getInvy();

    public STTrueFalse xgetInvy();

    public boolean isSetInvy();

    public void setInvy(STTrueFalse.Enum var1);

    public void xsetInvy(STTrueFalse var1);

    public void unsetInvy();

    public STTrueFalseBlank.Enum getSwitch();

    public STTrueFalseBlank xgetSwitch();

    public boolean isSetSwitch();

    public void setSwitch(STTrueFalseBlank.Enum var1);

    public void xsetSwitch(STTrueFalseBlank var1);

    public void unsetSwitch();

    public String getXrange();

    public XmlString xgetXrange();

    public boolean isSetXrange();

    public void setXrange(String var1);

    public void xsetXrange(XmlString var1);

    public void unsetXrange();

    public String getYrange();

    public XmlString xgetYrange();

    public boolean isSetYrange();

    public void setYrange(String var1);

    public void xsetYrange(XmlString var1);

    public void unsetYrange();

    public String getRadiusrange();

    public XmlString xgetRadiusrange();

    public boolean isSetRadiusrange();

    public void setRadiusrange(String var1);

    public void xsetRadiusrange(XmlString var1);

    public void unsetRadiusrange();
}

