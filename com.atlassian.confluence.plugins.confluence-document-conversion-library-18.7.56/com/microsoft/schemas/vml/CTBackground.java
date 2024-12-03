/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.STScreenSize
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.office.STBWMode;
import com.microsoft.schemas.office.office.STScreenSize;
import com.microsoft.schemas.vml.CTFill;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTBackground
extends XmlObject {
    public static final DocumentFactory<CTBackground> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbackgroundd4ectype");
    public static final SchemaType type = Factory.getType();

    public CTFill getFill();

    public boolean isSetFill();

    public void setFill(CTFill var1);

    public CTFill addNewFill();

    public void unsetFill();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public STTrueFalse.Enum getFilled();

    public STTrueFalse xgetFilled();

    public boolean isSetFilled();

    public void setFilled(STTrueFalse.Enum var1);

    public void xsetFilled(STTrueFalse var1);

    public void unsetFilled();

    public String getFillcolor();

    public STColorType xgetFillcolor();

    public boolean isSetFillcolor();

    public void setFillcolor(String var1);

    public void xsetFillcolor(STColorType var1);

    public void unsetFillcolor();

    public STBWMode.Enum getBwmode();

    public STBWMode xgetBwmode();

    public boolean isSetBwmode();

    public void setBwmode(STBWMode.Enum var1);

    public void xsetBwmode(STBWMode var1);

    public void unsetBwmode();

    public STBWMode.Enum getBwpure();

    public STBWMode xgetBwpure();

    public boolean isSetBwpure();

    public void setBwpure(STBWMode.Enum var1);

    public void xsetBwpure(STBWMode var1);

    public void unsetBwpure();

    public STBWMode.Enum getBwnormal();

    public STBWMode xgetBwnormal();

    public boolean isSetBwnormal();

    public void setBwnormal(STBWMode.Enum var1);

    public void xsetBwnormal(STBWMode var1);

    public void unsetBwnormal();

    public STScreenSize.Enum getTargetscreensize();

    public STScreenSize xgetTargetscreensize();

    public boolean isSetTargetscreensize();

    public void setTargetscreensize(STScreenSize.Enum var1);

    public void xsetTargetscreensize(STScreenSize var1);

    public void unsetTargetscreensize();
}

