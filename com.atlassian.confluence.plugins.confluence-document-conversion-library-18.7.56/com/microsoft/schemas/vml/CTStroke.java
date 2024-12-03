/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTStrokeChild
 *  com.microsoft.schemas.vml.STImageAspect
 *  com.microsoft.schemas.vml.STStrokeArrowLength
 *  com.microsoft.schemas.vml.STStrokeArrowWidth
 *  com.microsoft.schemas.vml.STStrokeEndCap
 *  com.microsoft.schemas.vml.STStrokeLineStyle
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.office.CTStrokeChild;
import com.microsoft.schemas.vml.STFillType;
import com.microsoft.schemas.vml.STImageAspect;
import com.microsoft.schemas.vml.STStrokeArrowLength;
import com.microsoft.schemas.vml.STStrokeArrowType;
import com.microsoft.schemas.vml.STStrokeArrowWidth;
import com.microsoft.schemas.vml.STStrokeEndCap;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import com.microsoft.schemas.vml.STStrokeLineStyle;
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

public interface CTStroke
extends XmlObject {
    public static final DocumentFactory<CTStroke> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstrokee2f6type");
    public static final SchemaType type = Factory.getType();

    public CTStrokeChild getLeft();

    public boolean isSetLeft();

    public void setLeft(CTStrokeChild var1);

    public CTStrokeChild addNewLeft();

    public void unsetLeft();

    public CTStrokeChild getTop();

    public boolean isSetTop();

    public void setTop(CTStrokeChild var1);

    public CTStrokeChild addNewTop();

    public void unsetTop();

    public CTStrokeChild getRight();

    public boolean isSetRight();

    public void setRight(CTStrokeChild var1);

    public CTStrokeChild addNewRight();

    public void unsetRight();

    public CTStrokeChild getBottom();

    public boolean isSetBottom();

    public void setBottom(CTStrokeChild var1);

    public CTStrokeChild addNewBottom();

    public void unsetBottom();

    public CTStrokeChild getColumn();

    public boolean isSetColumn();

    public void setColumn(CTStrokeChild var1);

    public CTStrokeChild addNewColumn();

    public void unsetColumn();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public STTrueFalse.Enum getOn();

    public STTrueFalse xgetOn();

    public boolean isSetOn();

    public void setOn(STTrueFalse.Enum var1);

    public void xsetOn(STTrueFalse var1);

    public void unsetOn();

    public String getWeight();

    public XmlString xgetWeight();

    public boolean isSetWeight();

    public void setWeight(String var1);

    public void xsetWeight(XmlString var1);

    public void unsetWeight();

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

    public STStrokeLineStyle.Enum getLinestyle();

    public STStrokeLineStyle xgetLinestyle();

    public boolean isSetLinestyle();

    public void setLinestyle(STStrokeLineStyle.Enum var1);

    public void xsetLinestyle(STStrokeLineStyle var1);

    public void unsetLinestyle();

    public BigDecimal getMiterlimit();

    public XmlDecimal xgetMiterlimit();

    public boolean isSetMiterlimit();

    public void setMiterlimit(BigDecimal var1);

    public void xsetMiterlimit(XmlDecimal var1);

    public void unsetMiterlimit();

    public STStrokeJoinStyle.Enum getJoinstyle();

    public STStrokeJoinStyle xgetJoinstyle();

    public boolean isSetJoinstyle();

    public void setJoinstyle(STStrokeJoinStyle.Enum var1);

    public void xsetJoinstyle(STStrokeJoinStyle var1);

    public void unsetJoinstyle();

    public STStrokeEndCap.Enum getEndcap();

    public STStrokeEndCap xgetEndcap();

    public boolean isSetEndcap();

    public void setEndcap(STStrokeEndCap.Enum var1);

    public void xsetEndcap(STStrokeEndCap var1);

    public void unsetEndcap();

    public String getDashstyle();

    public XmlString xgetDashstyle();

    public boolean isSetDashstyle();

    public void setDashstyle(String var1);

    public void xsetDashstyle(XmlString var1);

    public void unsetDashstyle();

    public STFillType.Enum getFilltype();

    public STFillType xgetFilltype();

    public boolean isSetFilltype();

    public void setFilltype(STFillType.Enum var1);

    public void xsetFilltype(STFillType var1);

    public void unsetFilltype();

    public String getSrc();

    public XmlString xgetSrc();

    public boolean isSetSrc();

    public void setSrc(String var1);

    public void xsetSrc(XmlString var1);

    public void unsetSrc();

    public STImageAspect.Enum getImageaspect();

    public STImageAspect xgetImageaspect();

    public boolean isSetImageaspect();

    public void setImageaspect(STImageAspect.Enum var1);

    public void xsetImageaspect(STImageAspect var1);

    public void unsetImageaspect();

    public String getImagesize();

    public XmlString xgetImagesize();

    public boolean isSetImagesize();

    public void setImagesize(String var1);

    public void xsetImagesize(XmlString var1);

    public void unsetImagesize();

    public STTrueFalse.Enum getImagealignshape();

    public STTrueFalse xgetImagealignshape();

    public boolean isSetImagealignshape();

    public void setImagealignshape(STTrueFalse.Enum var1);

    public void xsetImagealignshape(STTrueFalse var1);

    public void unsetImagealignshape();

    public String getColor2();

    public STColorType xgetColor2();

    public boolean isSetColor2();

    public void setColor2(String var1);

    public void xsetColor2(STColorType var1);

    public void unsetColor2();

    public STStrokeArrowType.Enum getStartarrow();

    public STStrokeArrowType xgetStartarrow();

    public boolean isSetStartarrow();

    public void setStartarrow(STStrokeArrowType.Enum var1);

    public void xsetStartarrow(STStrokeArrowType var1);

    public void unsetStartarrow();

    public STStrokeArrowWidth.Enum getStartarrowwidth();

    public STStrokeArrowWidth xgetStartarrowwidth();

    public boolean isSetStartarrowwidth();

    public void setStartarrowwidth(STStrokeArrowWidth.Enum var1);

    public void xsetStartarrowwidth(STStrokeArrowWidth var1);

    public void unsetStartarrowwidth();

    public STStrokeArrowLength.Enum getStartarrowlength();

    public STStrokeArrowLength xgetStartarrowlength();

    public boolean isSetStartarrowlength();

    public void setStartarrowlength(STStrokeArrowLength.Enum var1);

    public void xsetStartarrowlength(STStrokeArrowLength var1);

    public void unsetStartarrowlength();

    public STStrokeArrowType.Enum getEndarrow();

    public STStrokeArrowType xgetEndarrow();

    public boolean isSetEndarrow();

    public void setEndarrow(STStrokeArrowType.Enum var1);

    public void xsetEndarrow(STStrokeArrowType var1);

    public void unsetEndarrow();

    public STStrokeArrowWidth.Enum getEndarrowwidth();

    public STStrokeArrowWidth xgetEndarrowwidth();

    public boolean isSetEndarrowwidth();

    public void setEndarrowwidth(STStrokeArrowWidth.Enum var1);

    public void xsetEndarrowwidth(STStrokeArrowWidth var1);

    public void unsetEndarrowwidth();

    public STStrokeArrowLength.Enum getEndarrowlength();

    public STStrokeArrowLength xgetEndarrowlength();

    public boolean isSetEndarrowlength();

    public void setEndarrowlength(STStrokeArrowLength.Enum var1);

    public void xsetEndarrowlength(STStrokeArrowLength var1);

    public void unsetEndarrowlength();

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

    public String getTitle();

    public XmlString xgetTitle();

    public boolean isSetTitle();

    public void setTitle(String var1);

    public void xsetTitle(XmlString var1);

    public void unsetTitle();

    public STTrueFalse.Enum getForcedash();

    public STTrueFalse xgetForcedash();

    public boolean isSetForcedash();

    public void setForcedash(STTrueFalse.Enum var1);

    public void xsetForcedash(STTrueFalse var1);

    public void unsetForcedash();

    public String getId2();

    public STRelationshipId xgetId2();

    public boolean isSetId2();

    public void setId2(String var1);

    public void xsetId2(STRelationshipId var1);

    public void unsetId2();

    public STTrueFalse.Enum getInsetpen();

    public STTrueFalse xgetInsetpen();

    public boolean isSetInsetpen();

    public void setInsetpen(STTrueFalse.Enum var1);

    public void xsetInsetpen(STTrueFalse var1);

    public void unsetInsetpen();

    public String getRelid();

    public STRelationshipId xgetRelid();

    public boolean isSetRelid();

    public void setRelid(String var1);

    public void xsetRelid(STRelationshipId var1);

    public void unsetRelid();
}

