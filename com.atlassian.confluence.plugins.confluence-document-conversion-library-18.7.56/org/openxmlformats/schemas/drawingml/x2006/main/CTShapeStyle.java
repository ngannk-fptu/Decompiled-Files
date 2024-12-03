/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;

public interface CTShapeStyle
extends XmlObject {
    public static final DocumentFactory<CTShapeStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapestyle81ebtype");
    public static final SchemaType type = Factory.getType();

    public CTStyleMatrixReference getLnRef();

    public void setLnRef(CTStyleMatrixReference var1);

    public CTStyleMatrixReference addNewLnRef();

    public CTStyleMatrixReference getFillRef();

    public void setFillRef(CTStyleMatrixReference var1);

    public CTStyleMatrixReference addNewFillRef();

    public CTStyleMatrixReference getEffectRef();

    public void setEffectRef(CTStyleMatrixReference var1);

    public CTStyleMatrixReference addNewEffectRef();

    public CTFontReference getFontRef();

    public void setFontRef(CTFontReference var1);

    public CTFontReference addNewFontRef();
}

