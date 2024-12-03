/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTImageData
extends XmlObject {
    public static final DocumentFactory<CTImageData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctimagedata4039type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public String getSrc();

    public XmlString xgetSrc();

    public boolean isSetSrc();

    public void setSrc(String var1);

    public void xsetSrc(XmlString var1);

    public void unsetSrc();

    public String getCropleft();

    public XmlString xgetCropleft();

    public boolean isSetCropleft();

    public void setCropleft(String var1);

    public void xsetCropleft(XmlString var1);

    public void unsetCropleft();

    public String getCroptop();

    public XmlString xgetCroptop();

    public boolean isSetCroptop();

    public void setCroptop(String var1);

    public void xsetCroptop(XmlString var1);

    public void unsetCroptop();

    public String getCropright();

    public XmlString xgetCropright();

    public boolean isSetCropright();

    public void setCropright(String var1);

    public void xsetCropright(XmlString var1);

    public void unsetCropright();

    public String getCropbottom();

    public XmlString xgetCropbottom();

    public boolean isSetCropbottom();

    public void setCropbottom(String var1);

    public void xsetCropbottom(XmlString var1);

    public void unsetCropbottom();

    public String getGain();

    public XmlString xgetGain();

    public boolean isSetGain();

    public void setGain(String var1);

    public void xsetGain(XmlString var1);

    public void unsetGain();

    public String getBlacklevel();

    public XmlString xgetBlacklevel();

    public boolean isSetBlacklevel();

    public void setBlacklevel(String var1);

    public void xsetBlacklevel(XmlString var1);

    public void unsetBlacklevel();

    public String getGamma();

    public XmlString xgetGamma();

    public boolean isSetGamma();

    public void setGamma(String var1);

    public void xsetGamma(XmlString var1);

    public void unsetGamma();

    public STTrueFalse.Enum getGrayscale();

    public STTrueFalse xgetGrayscale();

    public boolean isSetGrayscale();

    public void setGrayscale(STTrueFalse.Enum var1);

    public void xsetGrayscale(STTrueFalse var1);

    public void unsetGrayscale();

    public STTrueFalse.Enum getBilevel();

    public STTrueFalse xgetBilevel();

    public boolean isSetBilevel();

    public void setBilevel(STTrueFalse.Enum var1);

    public void xsetBilevel(STTrueFalse var1);

    public void unsetBilevel();

    public String getChromakey();

    public STColorType xgetChromakey();

    public boolean isSetChromakey();

    public void setChromakey(String var1);

    public void xsetChromakey(STColorType var1);

    public void unsetChromakey();

    public String getEmbosscolor();

    public STColorType xgetEmbosscolor();

    public boolean isSetEmbosscolor();

    public void setEmbosscolor(String var1);

    public void xsetEmbosscolor(STColorType var1);

    public void unsetEmbosscolor();

    public String getRecolortarget();

    public STColorType xgetRecolortarget();

    public boolean isSetRecolortarget();

    public void setRecolortarget(String var1);

    public void xsetRecolortarget(STColorType var1);

    public void unsetRecolortarget();

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

    public float getOleid();

    public XmlFloat xgetOleid();

    public boolean isSetOleid();

    public void setOleid(float var1);

    public void xsetOleid(XmlFloat var1);

    public void unsetOleid();

    public STTrueFalse.Enum getDetectmouseclick();

    public STTrueFalse xgetDetectmouseclick();

    public boolean isSetDetectmouseclick();

    public void setDetectmouseclick(STTrueFalse.Enum var1);

    public void xsetDetectmouseclick(STTrueFalse var1);

    public void unsetDetectmouseclick();

    public float getMovie();

    public XmlFloat xgetMovie();

    public boolean isSetMovie();

    public void setMovie(float var1);

    public void xsetMovie(XmlFloat var1);

    public void unsetMovie();

    public String getRelid();

    public STRelationshipId xgetRelid();

    public boolean isSetRelid();

    public void setRelid(String var1);

    public void xsetRelid(STRelationshipId var1);

    public void unsetRelid();

    public String getId2();

    public STRelationshipId xgetId2();

    public boolean isSetId2();

    public void setId2(String var1);

    public void xsetId2(STRelationshipId var1);

    public void unsetId2();

    public String getPict();

    public STRelationshipId xgetPict();

    public boolean isSetPict();

    public void setPict(String var1);

    public void xsetPict(STRelationshipId var1);

    public void unsetPict();

    public String getHref2();

    public STRelationshipId xgetHref2();

    public boolean isSetHref2();

    public void setHref2(String var1);

    public void xsetHref2(STRelationshipId var1);

    public void unsetHref2();
}

