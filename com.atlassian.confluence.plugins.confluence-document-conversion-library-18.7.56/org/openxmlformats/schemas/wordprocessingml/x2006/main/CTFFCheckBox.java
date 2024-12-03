/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;

public interface CTFFCheckBox
extends XmlObject {
    public static final DocumentFactory<CTFFCheckBox> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctffcheckboxf3a5type");
    public static final SchemaType type = Factory.getType();

    public CTHpsMeasure getSize();

    public boolean isSetSize();

    public void setSize(CTHpsMeasure var1);

    public CTHpsMeasure addNewSize();

    public void unsetSize();

    public CTOnOff getSizeAuto();

    public boolean isSetSizeAuto();

    public void setSizeAuto(CTOnOff var1);

    public CTOnOff addNewSizeAuto();

    public void unsetSizeAuto();

    public CTOnOff getDefault();

    public boolean isSetDefault();

    public void setDefault(CTOnOff var1);

    public CTOnOff addNewDefault();

    public void unsetDefault();

    public CTOnOff getChecked();

    public boolean isSetChecked();

    public void setChecked(CTOnOff var1);

    public CTOnOff addNewChecked();

    public void unsetChecked();
}

