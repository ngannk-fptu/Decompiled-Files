/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDisplacedByCustomXml;

public interface CTMarkupRange
extends CTMarkup {
    public static final DocumentFactory<CTMarkupRange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmarkuprangeba3dtype");
    public static final SchemaType type = Factory.getType();

    public STDisplacedByCustomXml.Enum getDisplacedByCustomXml();

    public STDisplacedByCustomXml xgetDisplacedByCustomXml();

    public boolean isSetDisplacedByCustomXml();

    public void setDisplacedByCustomXml(STDisplacedByCustomXml.Enum var1);

    public void xsetDisplacedByCustomXml(STDisplacedByCustomXml var1);

    public void unsetDisplacedByCustomXml();
}

