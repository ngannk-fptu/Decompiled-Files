/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.word.STHorizontalAnchor
 *  com.microsoft.schemas.office.word.STVerticalAnchor
 *  com.microsoft.schemas.office.word.STWrapSide
 */
package com.microsoft.schemas.office.word;

import com.microsoft.schemas.office.word.STHorizontalAnchor;
import com.microsoft.schemas.office.word.STVerticalAnchor;
import com.microsoft.schemas.office.word.STWrapSide;
import com.microsoft.schemas.office.word.STWrapType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTWrap
extends XmlObject {
    public static final DocumentFactory<CTWrap> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctwrapbc3btype");
    public static final SchemaType type = Factory.getType();

    public STWrapType.Enum getType();

    public STWrapType xgetType();

    public boolean isSetType();

    public void setType(STWrapType.Enum var1);

    public void xsetType(STWrapType var1);

    public void unsetType();

    public STWrapSide.Enum getSide();

    public STWrapSide xgetSide();

    public boolean isSetSide();

    public void setSide(STWrapSide.Enum var1);

    public void xsetSide(STWrapSide var1);

    public void unsetSide();

    public STHorizontalAnchor.Enum getAnchorx();

    public STHorizontalAnchor xgetAnchorx();

    public boolean isSetAnchorx();

    public void setAnchorx(STHorizontalAnchor.Enum var1);

    public void xsetAnchorx(STHorizontalAnchor var1);

    public void unsetAnchorx();

    public STVerticalAnchor.Enum getAnchory();

    public STVerticalAnchor xgetAnchory();

    public boolean isSetAnchory();

    public void setAnchory(STVerticalAnchor.Enum var1);

    public void xsetAnchory(STVerticalAnchor var1);

    public void unsetAnchory();
}

