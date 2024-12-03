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

public interface CTTextPath
extends XmlObject {
    public static final DocumentFactory<CTTextPath> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextpath14f0type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public String getStyle();

    public XmlString xgetStyle();

    public boolean isSetStyle();

    public void setStyle(String var1);

    public void xsetStyle(XmlString var1);

    public void unsetStyle();

    public STTrueFalse.Enum getOn();

    public STTrueFalse xgetOn();

    public boolean isSetOn();

    public void setOn(STTrueFalse.Enum var1);

    public void xsetOn(STTrueFalse var1);

    public void unsetOn();

    public STTrueFalse.Enum getFitshape();

    public STTrueFalse xgetFitshape();

    public boolean isSetFitshape();

    public void setFitshape(STTrueFalse.Enum var1);

    public void xsetFitshape(STTrueFalse var1);

    public void unsetFitshape();

    public STTrueFalse.Enum getFitpath();

    public STTrueFalse xgetFitpath();

    public boolean isSetFitpath();

    public void setFitpath(STTrueFalse.Enum var1);

    public void xsetFitpath(STTrueFalse var1);

    public void unsetFitpath();

    public STTrueFalse.Enum getTrim();

    public STTrueFalse xgetTrim();

    public boolean isSetTrim();

    public void setTrim(STTrueFalse.Enum var1);

    public void xsetTrim(STTrueFalse var1);

    public void unsetTrim();

    public STTrueFalse.Enum getXscale();

    public STTrueFalse xgetXscale();

    public boolean isSetXscale();

    public void setXscale(STTrueFalse.Enum var1);

    public void xsetXscale(STTrueFalse var1);

    public void unsetXscale();

    public String getString();

    public XmlString xgetString();

    public boolean isSetString();

    public void setString(String var1);

    public void xsetString(XmlString var1);

    public void unsetString();
}

