/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTFill
 *  com.microsoft.schemas.vml.STImageAspect
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.vml.STFillMethod;
import com.microsoft.schemas.vml.STFillType;
import com.microsoft.schemas.vml.STImageAspect;
import java.math.BigDecimal;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTFill
extends XmlObject {
    public static final DocumentFactory<CTFill> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfillb241type");
    public static final SchemaType type = Factory.getType();

    public com.microsoft.schemas.office.office.CTFill getFill();

    public boolean isSetFill();

    public void setFill(com.microsoft.schemas.office.office.CTFill var1);

    public com.microsoft.schemas.office.office.CTFill addNewFill();

    public void unsetFill();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public STFillType.Enum getType();

    public STFillType xgetType();

    public boolean isSetType();

    public void setType(STFillType.Enum var1);

    public void xsetType(STFillType var1);

    public void unsetType();

    public STTrueFalse.Enum getOn();

    public STTrueFalse xgetOn();

    public boolean isSetOn();

    public void setOn(STTrueFalse.Enum var1);

    public void xsetOn(STTrueFalse var1);

    public void unsetOn();

    public String getColor();

    public STColorType xgetColor();

    public boolean isSetColor();

    public void setColor(String var1);

    public void xsetColor(STColorType var1);

    public void unsetColor();

    public String getOpacity();

    public XmlString xgetOpacity();

    public boolean isSetOpacity();

    public void setOpacity(String var1);

    public void xsetOpacity(XmlString var1);

    public void unsetOpacity();

    public String getColor2();

    public STColorType xgetColor2();

    public boolean isSetColor2();

    public void setColor2(String var1);

    public void xsetColor2(STColorType var1);

    public void unsetColor2();

    public String getSrc();

    public XmlString xgetSrc();

    public boolean isSetSrc();

    public void setSrc(String var1);

    public void xsetSrc(XmlString var1);

    public void unsetSrc();

    public String getHref();

    public XmlString xgetHref();

    public boolean isSetHref();

    public void setHref(String var1);

    public void xsetHref(XmlString var1);

    public void unsetHref();

    public String getAlthref();

    public XmlString xgetAlthref();

    public boolean isSetAlthref();

    public void setAlthref(String var1);

    public void xsetAlthref(XmlString var1);

    public void unsetAlthref();

    public String getSize();

    public XmlString xgetSize();

    public boolean isSetSize();

    public void setSize(String var1);

    public void xsetSize(XmlString var1);

    public void unsetSize();

    public String getOrigin();

    public XmlString xgetOrigin();

    public boolean isSetOrigin();

    public void setOrigin(String var1);

    public void xsetOrigin(XmlString var1);

    public void unsetOrigin();

    public String getPosition();

    public XmlString xgetPosition();

    public boolean isSetPosition();

    public void setPosition(String var1);

    public void xsetPosition(XmlString var1);

    public void unsetPosition();

    public STImageAspect.Enum getAspect();

    public STImageAspect xgetAspect();

    public boolean isSetAspect();

    public void setAspect(STImageAspect.Enum var1);

    public void xsetAspect(STImageAspect var1);

    public void unsetAspect();

    public String getColors();

    public XmlString xgetColors();

    public boolean isSetColors();

    public void setColors(String var1);

    public void xsetColors(XmlString var1);

    public void unsetColors();

    public BigDecimal getAngle();

    public XmlDecimal xgetAngle();

    public boolean isSetAngle();

    public void setAngle(BigDecimal var1);

    public void xsetAngle(XmlDecimal var1);

    public void unsetAngle();

    public STTrueFalse.Enum getAlignshape();

    public STTrueFalse xgetAlignshape();

    public boolean isSetAlignshape();

    public void setAlignshape(STTrueFalse.Enum var1);

    public void xsetAlignshape(STTrueFalse var1);

    public void unsetAlignshape();

    public String getFocus();

    public XmlString xgetFocus();

    public boolean isSetFocus();

    public void setFocus(String var1);

    public void xsetFocus(XmlString var1);

    public void unsetFocus();

    public String getFocussize();

    public XmlString xgetFocussize();

    public boolean isSetFocussize();

    public void setFocussize(String var1);

    public void xsetFocussize(XmlString var1);

    public void unsetFocussize();

    public String getFocusposition();

    public XmlString xgetFocusposition();

    public boolean isSetFocusposition();

    public void setFocusposition(String var1);

    public void xsetFocusposition(XmlString var1);

    public void unsetFocusposition();

    public STFillMethod.Enum getMethod();

    public STFillMethod xgetMethod();

    public boolean isSetMethod();

    public void setMethod(STFillMethod.Enum var1);

    public void xsetMethod(STFillMethod var1);

    public void unsetMethod();

    public STTrueFalse.Enum getDetectmouseclick();

    public STTrueFalse xgetDetectmouseclick();

    public boolean isSetDetectmouseclick();

    public void setDetectmouseclick(STTrueFalse.Enum var1);

    public void xsetDetectmouseclick(STTrueFalse var1);

    public void unsetDetectmouseclick();

    public String getTitle();

    public XmlString xgetTitle();

    public boolean isSetTitle();

    public void setTitle(String var1);

    public void xsetTitle(XmlString var1);

    public void unsetTitle();

    public String getOpacity2();

    public XmlString xgetOpacity2();

    public boolean isSetOpacity2();

    public void setOpacity2(String var1);

    public void xsetOpacity2(XmlString var1);

    public void unsetOpacity2();

    public STTrueFalse.Enum getRecolor();

    public STTrueFalse xgetRecolor();

    public boolean isSetRecolor();

    public void setRecolor(STTrueFalse.Enum var1);

    public void xsetRecolor(STTrueFalse var1);

    public void unsetRecolor();

    public STTrueFalse.Enum getRotate();

    public STTrueFalse xgetRotate();

    public boolean isSetRotate();

    public void setRotate(STTrueFalse.Enum var1);

    public void xsetRotate(STTrueFalse var1);

    public void unsetRotate();

    public String getId2();

    public STRelationshipId xgetId2();

    public boolean isSetId2();

    public void setId2(String var1);

    public void xsetId2(STRelationshipId var1);

    public void unsetId2();

    public String getRelid();

    public STRelationshipId xgetRelid();

    public boolean isSetRelid();

    public void setRelid(String var1);

    public void xsetRelid(STRelationshipId var1);

    public void unsetRelid();
}

