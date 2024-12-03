/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.vml.STShadowType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTShadow
extends XmlObject {
    public static final DocumentFactory<CTShadow> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshadowdfdetype");
    public static final SchemaType type = Factory.getType();

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

    public STShadowType.Enum getType();

    public STShadowType xgetType();

    public boolean isSetType();

    public void setType(STShadowType.Enum var1);

    public void xsetType(STShadowType var1);

    public void unsetType();

    public STTrueFalse.Enum getObscured();

    public STTrueFalse xgetObscured();

    public boolean isSetObscured();

    public void setObscured(STTrueFalse.Enum var1);

    public void xsetObscured(STTrueFalse var1);

    public void unsetObscured();

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

    public String getOffset();

    public XmlString xgetOffset();

    public boolean isSetOffset();

    public void setOffset(String var1);

    public void xsetOffset(XmlString var1);

    public void unsetOffset();

    public String getColor2();

    public STColorType xgetColor2();

    public boolean isSetColor2();

    public void setColor2(String var1);

    public void xsetColor2(STColorType var1);

    public void unsetColor2();

    public String getOffset2();

    public XmlString xgetOffset2();

    public boolean isSetOffset2();

    public void setOffset2(String var1);

    public void xsetOffset2(XmlString var1);

    public void unsetOffset2();

    public String getOrigin();

    public XmlString xgetOrigin();

    public boolean isSetOrigin();

    public void setOrigin(String var1);

    public void xsetOrigin(XmlString var1);

    public void unsetOrigin();

    public String getMatrix();

    public XmlString xgetMatrix();

    public boolean isSetMatrix();

    public void setMatrix(String var1);

    public void xsetMatrix(XmlString var1);

    public void unsetMatrix();
}

