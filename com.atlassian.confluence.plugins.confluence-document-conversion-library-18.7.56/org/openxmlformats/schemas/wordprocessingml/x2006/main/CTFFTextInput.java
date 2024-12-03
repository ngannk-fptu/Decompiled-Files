/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFTextType
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFTextType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

public interface CTFFTextInput
extends XmlObject {
    public static final DocumentFactory<CTFFTextInput> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfftextinput3155type");
    public static final SchemaType type = Factory.getType();

    public CTFFTextType getType();

    public boolean isSetType();

    public void setType(CTFFTextType var1);

    public CTFFTextType addNewType();

    public void unsetType();

    public CTString getDefault();

    public boolean isSetDefault();

    public void setDefault(CTString var1);

    public CTString addNewDefault();

    public void unsetDefault();

    public CTDecimalNumber getMaxLength();

    public boolean isSetMaxLength();

    public void setMaxLength(CTDecimalNumber var1);

    public CTDecimalNumber addNewMaxLength();

    public void unsetMaxLength();

    public CTString getFormat();

    public boolean isSetFormat();

    public void setFormat(CTString var1);

    public CTString addNewFormat();

    public void unsetFormat();
}

