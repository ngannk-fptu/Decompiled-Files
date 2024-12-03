/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;

public interface CTTextBlipBullet
extends XmlObject {
    public static final DocumentFactory<CTTextBlipBullet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextblipbullet853btype");
    public static final SchemaType type = Factory.getType();

    public CTBlip getBlip();

    public void setBlip(CTBlip var1);

    public CTBlip addNewBlip();
}

