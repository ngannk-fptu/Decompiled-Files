/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;

public interface CTStretchInfoProperties
extends XmlObject {
    public static final DocumentFactory<CTStretchInfoProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctstretchinfopropertiesde57type");
    public static final SchemaType type = Factory.getType();

    public CTRelativeRect getFillRect();

    public boolean isSetFillRect();

    public void setFillRect(CTRelativeRect var1);

    public CTRelativeRect addNewFillRect();

    public void unsetFillRect();
}

