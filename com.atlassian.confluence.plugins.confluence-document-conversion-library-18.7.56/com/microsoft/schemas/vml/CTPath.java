/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.office.STConnectType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTPath
extends XmlObject {
    public static final DocumentFactory<CTPath> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath5963type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public String getV();

    public XmlString xgetV();

    public boolean isSetV();

    public void setV(String var1);

    public void xsetV(XmlString var1);

    public void unsetV();

    public String getLimo();

    public XmlString xgetLimo();

    public boolean isSetLimo();

    public void setLimo(String var1);

    public void xsetLimo(XmlString var1);

    public void unsetLimo();

    public String getTextboxrect();

    public XmlString xgetTextboxrect();

    public boolean isSetTextboxrect();

    public void setTextboxrect(String var1);

    public void xsetTextboxrect(XmlString var1);

    public void unsetTextboxrect();

    public STTrueFalse.Enum getFillok();

    public STTrueFalse xgetFillok();

    public boolean isSetFillok();

    public void setFillok(STTrueFalse.Enum var1);

    public void xsetFillok(STTrueFalse var1);

    public void unsetFillok();

    public STTrueFalse.Enum getStrokeok();

    public STTrueFalse xgetStrokeok();

    public boolean isSetStrokeok();

    public void setStrokeok(STTrueFalse.Enum var1);

    public void xsetStrokeok(STTrueFalse var1);

    public void unsetStrokeok();

    public STTrueFalse.Enum getShadowok();

    public STTrueFalse xgetShadowok();

    public boolean isSetShadowok();

    public void setShadowok(STTrueFalse.Enum var1);

    public void xsetShadowok(STTrueFalse var1);

    public void unsetShadowok();

    public STTrueFalse.Enum getArrowok();

    public STTrueFalse xgetArrowok();

    public boolean isSetArrowok();

    public void setArrowok(STTrueFalse.Enum var1);

    public void xsetArrowok(STTrueFalse var1);

    public void unsetArrowok();

    public STTrueFalse.Enum getGradientshapeok();

    public STTrueFalse xgetGradientshapeok();

    public boolean isSetGradientshapeok();

    public void setGradientshapeok(STTrueFalse.Enum var1);

    public void xsetGradientshapeok(STTrueFalse var1);

    public void unsetGradientshapeok();

    public STTrueFalse.Enum getTextpathok();

    public STTrueFalse xgetTextpathok();

    public boolean isSetTextpathok();

    public void setTextpathok(STTrueFalse.Enum var1);

    public void xsetTextpathok(STTrueFalse var1);

    public void unsetTextpathok();

    public STTrueFalse.Enum getInsetpenok();

    public STTrueFalse xgetInsetpenok();

    public boolean isSetInsetpenok();

    public void setInsetpenok(STTrueFalse.Enum var1);

    public void xsetInsetpenok(STTrueFalse var1);

    public void unsetInsetpenok();

    public STConnectType.Enum getConnecttype();

    public STConnectType xgetConnecttype();

    public boolean isSetConnecttype();

    public void setConnecttype(STConnectType.Enum var1);

    public void xsetConnecttype(STConnectType var1);

    public void unsetConnecttype();

    public String getConnectlocs();

    public XmlString xgetConnectlocs();

    public boolean isSetConnectlocs();

    public void setConnectlocs(String var1);

    public void xsetConnectlocs(XmlString var1);

    public void unsetConnectlocs();

    public String getConnectangles();

    public XmlString xgetConnectangles();

    public boolean isSetConnectangles();

    public void setConnectangles(String var1);

    public void xsetConnectangles(XmlString var1);

    public void unsetConnectangles();

    public STTrueFalse.Enum getExtrusionok();

    public STTrueFalse xgetExtrusionok();

    public boolean isSetExtrusionok();

    public void setExtrusionok(STTrueFalse.Enum var1);

    public void xsetExtrusionok(STTrueFalse var1);

    public void unsetExtrusionok();
}

