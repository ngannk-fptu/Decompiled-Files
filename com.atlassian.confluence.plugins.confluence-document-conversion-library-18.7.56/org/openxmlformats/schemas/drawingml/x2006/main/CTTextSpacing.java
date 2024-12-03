/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;

public interface CTTextSpacing
extends XmlObject {
    public static final DocumentFactory<CTTextSpacing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextspacingef87type");
    public static final SchemaType type = Factory.getType();

    public CTTextSpacingPercent getSpcPct();

    public boolean isSetSpcPct();

    public void setSpcPct(CTTextSpacingPercent var1);

    public CTTextSpacingPercent addNewSpcPct();

    public void unsetSpcPct();

    public CTTextSpacingPoint getSpcPts();

    public boolean isSetSpcPts();

    public void setSpcPts(CTTextSpacingPoint var1);

    public CTTextSpacingPoint addNewSpcPts();

    public void unsetSpcPts();
}

