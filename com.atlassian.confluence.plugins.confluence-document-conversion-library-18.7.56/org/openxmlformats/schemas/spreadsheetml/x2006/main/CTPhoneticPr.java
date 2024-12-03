/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticAlignment
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticType;

public interface CTPhoneticPr
extends XmlObject {
    public static final DocumentFactory<CTPhoneticPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctphoneticpr898btype");
    public static final SchemaType type = Factory.getType();

    public long getFontId();

    public STFontId xgetFontId();

    public void setFontId(long var1);

    public void xsetFontId(STFontId var1);

    public STPhoneticType.Enum getType();

    public STPhoneticType xgetType();

    public boolean isSetType();

    public void setType(STPhoneticType.Enum var1);

    public void xsetType(STPhoneticType var1);

    public void unsetType();

    public STPhoneticAlignment.Enum getAlignment();

    public STPhoneticAlignment xgetAlignment();

    public boolean isSetAlignment();

    public void setAlignment(STPhoneticAlignment.Enum var1);

    public void xsetAlignment(STPhoneticAlignment var1);

    public void unsetAlignment();
}

